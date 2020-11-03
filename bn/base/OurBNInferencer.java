package bn.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.CPT;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.inference.RejectionSampling;
import bn.inference.LikelihoodWeighting;
import bn.inference.EnumerationInferencer;
import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.NamedVariable;
import bn.util.ArraySet;
import org.xml.sax.*;
import bn.parser.*;

public class OurBNInferencer { 
    public static void main(String[] args)throws IOException, ParserConfigurationException, SAXException{
        String filename = args[0];
        String filename2 = filename.toLowerCase();
        BayesianNetwork network = new bn.base.BayesianNetwork();
        if(filename2.contains(".xml")){
            XMLBIFParser parser = new XMLBIFParser();
            network = parser.readNetworkFromFile(filename);
        }else if (filename2.contains(".bif")){
            BIFParser parser = new BIFParser(new FileInputStream(filename));
			network = parser.parseNetwork();
        }    
        Assignment a = new bn.base.Assignment();
        for (int i = 2; i < args.length; i+=2){
            RandomVariable r = network.getVariableByName(args[i]);
            Value v = new Value(args[i+1]);
            a.put(r, v);
        }

        EnumerationInferencer exact = new EnumerationInferencer();
        Distribution dist = exact.query(network.getVariableByName(args[1]), a, network);
        System.out.println(dist);

        
    }
}
