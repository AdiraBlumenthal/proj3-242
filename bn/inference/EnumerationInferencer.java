package bn.inference;

import bn.core.Value;
// import jdk.internal.jshell.tool.resources.l10n;

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

    public Distribution enumerationAsk(RandomVariable X, Assignment e, BayesianNetwork bn){
        Distribution Q = new bn.base.Distribution(X);
        for (Value xi : Q.getVariable().getDomain()) {
            //trying to set the double probability of the distribution q at the value xi
            e.put(X, xi);
            Q.set(xi, enumerateAll(bn.getVariablesSortedTopologically(), e, bn));   //where exi is evidence e plus the assignment x=xi
        }
        Q.normalize();
        return Q;
    }
   
    //public 
    
    public double enumerateAll(List<RandomVariable> vars, Assignment e, BayesianNetwork bn){//returns a probability
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
                 e.put(Y, yi);
                 sum += bn.getProbability(Y, e) * enumerateAll(vars, e, bn);
            }
            return sum;
        }
    }
}


// // function ENUMERATION-ASK(X, e, bn) returns a distribution over X 
// //        inputs: X, the query variable 
// //                e, observed values for variables E 
// //                bn, a Bayes net with variables {X} ∪ E ∪ Y  /* Y = hidden variables */
// //                 
// //        Q(X)←a distribution over X, initially empty 
// //        for each value xi of X do Q(xi) ← ENUMERATE-ALL(bn.VARS, exi)
// //                where exi is e extended with X= xi
// //        return NORMALIZE(Q(X))

// // function ENUMERATE-ALL(vars, e) returns a real number 
// //      if EMPTY?(vars) then return 1.0 
// //      Y←FIRST(vars) 
// //      if Y has value y in e 
// //          then return P(y | parents(Y)) × ENUMERATE-ALL(REST(vars), e) 
// //          else return Sum(y) P(y|parents(Y)) × ENUMERATE-ALL(REST(vars), ey)
// //              where ey is e extended with Y= y 