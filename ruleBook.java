/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spotlight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Abhay
 */
public class ruleBook {
    
    HashMap<String, HashMap<String, Integer> > PRPCount = new HashMap <>();
    HashMap<String, HashMap<String, Integer> > PRPVBPCount = new HashMap <>();
    
    public void add(String l, String r, String value)
    {
        if(value == "PRP$")
        {
            if(!PRPCount.containsKey(l))
                PRPCount.put(l, new HashMap<String, Integer>());
            if(!PRPCount.get(l).containsKey(r))
                PRPCount.get(l).put(r, 0);
            
            PRPCount.get(l).put(r, PRPCount.get(l).get(r).intValue()+1);
        }
        else
        {
            if(!PRPVBPCount.containsKey(l))
                PRPVBPCount.put(l, new HashMap<String, Integer>());
            if(!PRPVBPCount.get(l).containsKey(r))
                PRPVBPCount.get(l).put(r, 0);
            
            PRPVBPCount.get(l).put(r, PRPVBPCount.get(l).get(r).intValue()+1);
        }
    }
       
    public String getResult(String l, String r)
    {
        System.out.println("came : " + l + " : " + r);
        int total = 0, prp = 0;
        
        if(PRPCount.containsKey(l))
            if(PRPCount.get(l).containsKey(r))
                prp = PRPCount.get(l).get(r).intValue();
        
        System.out.println("prp = " + prp);
        
        total = prp;
        
        if(PRPVBPCount.containsKey(l))
            if(PRPVBPCount.get(l).containsKey(r))
                total += PRPVBPCount.get(l).get(r).intValue();
        
        System.out.println("total = " + total);
        
        if(total == 0)
            return getOnLeftOnly(l);
        
        double confidencePRP = (double)prp/(double)total;
        System.out.println("Confidence : " + confidencePRP);
        if(confidencePRP >= 0.5)
        {
            return "PRP$";
        }
        else
        {
            return "PRP_VBP";
        }
    }
    
    public String getOnLeftOnly(String l)
    {
        System.out.println("HERE");
        int prp = 0, total = 0;
        
        Iterator it;
        if(PRPCount.containsKey(l))
        {
            it = PRPCount.get(l).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                prp += Integer.parseInt(pairs.getValue().toString());
            }
        }
        
        total += prp;
        
        if(PRPVBPCount.containsKey(l))
        {
            it = PRPVBPCount.get(l).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                total += Integer.parseInt(pairs.getValue().toString());
            }
        }
        
        if(total == 0)
            return "PRP$";
        
        double confidencePRP = (double)prp/(double)total;
        System.out.println("ConfidenceL : " + confidencePRP);
        if(confidencePRP >= 0.5)
        {
            return "PRP$";
        }
        else
        {
            return "PRP_VBP";
        }
    }
}
