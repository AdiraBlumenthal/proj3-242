package bn.inference;
import bn.core.Value;
import java.util.*;
import bn.core.Domain;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Distribution;

public class LikelihoodWeighting implements Inferencer{
    public int sampleSize;
    public double weight;
    
    public void nSetter(int n){
        this.sampleSize = n;
    }
    
    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return likelihoodWeighting(sampleSize, X, e, network); //change back to samplesize (from 1)
    }

    public Distribution likelihoodWeighting(int sampleSize, RandomVariable X, Assignment e, BayesianNetwork bn){
        Distribution W = new bn.base.Distribution(X);
        for(Value v : X.getDomain()){
            W.set(v, 0.0);
        }
        for(int j = 1; j<sampleSize; j++){
            Assignment a = weightedSample(bn, e); //bolded x
            Value v = a.get(X);
            W.put(v, (W.get(a.get(X)) + weight));
        }
        W.normalize();
        return W;
    }
    
    public Assignment weightedSample(BayesianNetwork bn, Assignment e){
        weight = 1.0;
        Assignment a = e.copy(); // bolded x
        for(RandomVariable rand : bn.getVariablesSortedTopologically()){
            if(e.containsKey(rand)){
                weight = weight * bn.getProbability(rand, a);
            } else{
                Distribution dist = new bn.base.Distribution(rand);
                Distribution dist2 = new bn.base.Distribution(rand);
                double runSum = 0.0;
                for (Value v: rand.getDomain()){
                    a.put(rand, v);
                    dist.set(v, bn.getProbability(rand, a));
                    runSum += dist.get(v);
                    dist2.set(v, runSum);
                }
                double randomValue = Math.random();
                for (Value v: rand.getDomain()){
                    if(randomValue <= dist2.get(v)){
                        a.put(rand, v);
                        break;
                    }
                }
            }
        }
        return a;
    }
}
//  function WEIGHTED-SAMPLE(bn, e) returns an event and a weight 
//      w ← 1; 
//      x ← an event with n elements initialized from e 
//      for each variable Xi in X1,...,Xn do 
//      if Xi is an evidence variable with value xi in e        
//          then w ←w × P(Xi = xi | parents(Xi))        
//      else x[i] ← a random sample from P(Xi | parents(Xi )) 
//      return x, w 

// function LIKELIHOOD-WEIGHTING(X , e, bn, N ) returns an estimate of P(X|e) 
//  inputs: X , the query variable 
//          e, observed values for variables E         
//          bn, a Bayesian network specifying joint distribution 
//          P(X1, . . . , Xn) N , the total number of samples to be generated 
//  local variables: W, a vector of weighted counts for each value of X , initially zero 
//  for j =1 to N do        
//      x, w ← WEIGHTED-SAMPLE(bn, e) 
//      W[x]←W[x]+w where x is the value of X in x 
//  return NORMALIZE(W)

