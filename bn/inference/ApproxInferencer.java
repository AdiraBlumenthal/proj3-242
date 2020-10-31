package bn.inference;
import bn.core.Value;
import java.util.*;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Distribution;

public class ApproxInferencer implements Inferencer{
    
    public int sampleSize;
    public void nSetter(int n){
        this.sampleSize = n;
    }

    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        // int n = 0;
        // return rejectionSampling(n, X, e, network);
        return rejectionSampling(sampleSize, X, e, network);
    }
    
    public Distribution rejectionSampling(int sampleSize, RandomVariable X, Assignment e, BayesianNetwork bn){
        Distribution N = new bn.base.Distribution(X);
        //Value x = new Value();
        for(int j = 1; j < sampleSize; j++){
            Assignment a = priorSample(bn);
            if(a.checkConsistent(e, a)){
                System.out.println("here");
            }
        }
        return null;
    }

    public Assignment priorSample(BayesianNetwork bn){
        Assignment x = new bn.base.Assignment();
        List<RandomVariable> bigX = bn.getVariablesSortedTopologically();
        for(RandomVariable rand : bigX){
           // x.put(rand, );
        }
        return x;
    }
    
    // function PRIOR-SAMPLE(bn) returns an event sampled from the prior specified by bn 
    //     inputs: bn, a Bayesian network specifying joint distribution P(X1, . . . , Xn)
    //     x ← an event with n elements 
    //     for each variable Xi in X1,...,Xn do
    //         x[i] ← a random sample from P(Xi | parents(Xi)) 
    //     return x
}

// function REJECTION-SAMPLING(X , e, bn, N) returns an estimate of P(X|e)     
//  inputs: X, the query variable 
//          e, observed values for variables E          
//          bn, a Bayesian network          
//          N, the total number of samples to be generated 
//  local variables: N, a vector of counts for each value of X, initially zero 
//  
//  for j =1 to N do        
//      x ← PRIOR-SAMPLE(bn)        
//      if x is consistent with e then 
//          N[x] ← N[x]+1 where x is the value of X in x 
//  return NORMALIZE(N) 




// ————————————————————————————————————————————————


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