package com.teamdev.jxbrowser.chromium.demo.vo;

import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by huangyang on 17/3/31.
 */
public class DOMElementWrapper  {

    private DOMElement element ;

    private String originalStyle = "";

    public DOMElementWrapper(DOMElement element){
        this.element = element;
        String style = element.getAttribute("style");
        if(StringUtils.isNotBlank(style) && "".equals(style)){
            this.originalStyle = style;
        }
    }

    public String getNodeName(){
        return this.element.getNodeName();
    }

    public DOMElement getElement(){
        return this.element ;
    }

    public void resetStyle(){
        if(StringUtils.isNotBlank(this.originalStyle)){
            this.element.setAttribute("style",this.originalStyle);
        }else {
            this.element.setAttribute("style","border:none none none;");
        }
    }

    public void setMarkTargetStyle(){
         element.setAttribute("style", this.originalStyle + " ; " + "border:red dashed 1px;");
    }

}
