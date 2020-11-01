package bn.inference;
import bn.core.Value;
import java.util.*;


import bn.core.Domain;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Distribution;

public class RejectionSampling implements Inferencer{
    
    public int sampleSize;
    public void nSetter(int n){
        this.sampleSize = n;
    }

    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return rejectionSampling(sampleSize, X, e, network); //change back to samplesize (from 1)
    }
    
    public Distribution rejectionSampling(int sampleSize, RandomVariable X, Assignment e, BayesianNetwork bn){
        Distribution N = new bn.base.Distribution(X);
        for(Value v : X.getDomain()){
            N.set(v, 0.0);
        }
        for(int j = 1; j < sampleSize; j++){
            Assignment a = priorSample(bn); //bolded x
            if(a.containsAll(e)){
                Value v = a.get(X);
                N.put(v, (N.get(a.get(X)) + 1.0));
            }
        }
        N.normalize();
        return N;
    }

    public Assignment priorSample(BayesianNetwork bn){
        Assignment sample = new bn.base.Assignment();
        for(RandomVariable rand : bn.getVariablesSortedTopologically()){
            Distribution dist = new bn.base.Distribution(rand);
            Distribution dist2 = new bn.base.Distribution(rand);
            double runSum = 0.0;
            for (Value v: rand.getDomain()){
                sample.put(rand, v);
                dist.set(v, bn.getProbability(rand, sample));
                runSum += dist.get(v);
                dist2.set(v, runSum);
            }
            double randomValue = Math.random();
            for (Value v: rand.getDomain()){
                if(randomValue <= dist2.get(v)){
                    sample.put(rand, v);
                    break;
                }
            }
        }
        return sample;
    }
}


// function LIKELIHOOD-WEIGHTING(X , e, bn, N ) returns an estimate of P(X|e) 
//  inputs: X , the query variable 
//          e, observed values for variables E         
//          bn, a Bayesian network specifying joint distribution 
//          P(X1, . . . , Xn) N , the total number of samples to be generated 
//  local variables: W, a vector of weighted counts for each value of X , initially zero 
//  for j =1 to N do        x, w ← WEIGHTED-SAMPLE(bn, e) 
//      W[x]←W[x]+w where x is the value of X in x 
//  return NORMALIZE(W)

//  function WEIGHTED-SAMPLE(bn, e) returns an event and a weight 
//  w ← 1; x ← an event with n elements initialized from e 
//  for each variable Xi in X1,...,Xn do 
//  if Xi is an evidence variable with value xi in e        then w ←w × P(Xi = xi | parents(Xi))        else x[i] ← a random sample from P(Xi | parents(Xi )) 
//  return x, w 