package com.teamdev.jxbrowser.chromium.demo.util;

import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.dom.DOMNode;
import com.teamdev.jxbrowser.chromium.dom.DOMNodeType;
import org.xml.sax.XMLReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by huangyang on 17/4/5.
 */



public class XpathUtility {
    private Map<DOMNode, String> xpathCache = new HashMap();
    private Map<DOMNode, Integer> nodeIndexCache = new HashMap();


    public static String xpath(DOMElement element){

        return new XpathUtility().getElementXpath(element);
    }

    private String getElementXpath(DOMElement elt){
        StringBuilder path =new StringBuilder();

        for (DOMNode fib =  elt; fib != null; fib = fib.getParent()){

            if (fib.getNodeType() == DOMNodeType.ElementNode){

                String cachedParentPath = xpathCache.get(fib);

                if (cachedParentPath != null){
                    path.insert(0, cachedParentPath);
                    break;
                }
                int idx = getElementIdx(fib);
                StringBuilder xname = new StringBuilder(fib.getNodeName());

                if (idx >= 1) xname.append( "[" + idx + "]");
                xname.insert(0, "/");
                path.insert(0, xname);
            }
        }

        if (!xpathCache.containsKey( elt)){
            xpathCache.put ( elt, path.toString());
        }

        return path.toString();
    }

    private int getElementIdx(DOMNode elt) {
        Integer count = nodeIndexCache.get(elt);
        if (count != null){
            return count;
        }

        count = 1;
        for (DOMNode sib = elt.getPreviousSibling(); sib != null; sib = sib.getPreviousSibling())
        {
            if (sib.getNodeType() == DOMNodeType.ElementNode){

                if(sib.getNodeName().equals(elt.getNodeName())){
                    count++;
                }
            }
        }
        nodeIndexCache.put( elt, count);

        return count;
    }

}
