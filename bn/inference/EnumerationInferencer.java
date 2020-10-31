package bn.inference;

import bn.core.Value;
import java.util.*;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Distribution;


public class EnumerationInferencer implements Inferencer {

    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return enumerationAsk(X, e, network);
    }
    
    // @Override
    // public Distribution approxQuery(int i, RandomVariable X, Assignment e, BayesianNetwork network) {
    //     return null;
    // }

    public Distribution enumerationAsk(RandomVariable X, Assignment e, BayesianNetwork bn){
        Distribution Q = new bn.base.Distribution(X);
        //for (Value xi : Q.getVariable().getDomain()) {
        for (Value xi : X.getDomain()) {
            //trying to set the double probability of the distribution q at the value xi
            Assignment eCopy = e.copy();
            eCopy.put(X, xi);
            Q.put(xi, enumerateAll(bn.getVariablesSortedTopologically(), eCopy, bn));   //where exi is evidence e plus the assignment x=xi
        }
        Q.normalize();
        return Q;

    }
    
    public double enumerateAll(List<RandomVariable> vars, Assignment e, BayesianNetwork bn){ //returns a probability
        if(vars.isEmpty()){
            return 1.0;
        }
        RandomVariable Y = vars.get(0);
        vars.remove(0);
        if (e.containsKey(Y)){
            return bn.getProbability(Y, e) * enumerateAll(vars, e, bn);
        }else{
            double sum = 0;
            for (Value yi : Y.getDomain()){
                Assignment eCopy = e.copy();
                eCopy.put(Y, yi);
                List<RandomVariable> rVars = new ArrayList<RandomVariable>(vars);
                sum += bn.getProbability(Y, eCopy) * enumerateAll(rVars, eCopy, bn);
                e.remove(Y);
            }
            return sum;
        }
    }  
}
