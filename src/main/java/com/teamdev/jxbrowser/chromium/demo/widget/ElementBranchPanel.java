package com.teamdev.jxbrowser.chromium.demo.widget;

import com.teamdev.jxbrowser.chromium.demo.vo.DOMElementWrapper;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于展示当前点中元素的节点树
 */
public class ElementBranchPanel extends JPanel {

    private List<DOMElementWrapper> nodes ;

    private Integer clickedIndex ;


    public ElementBranchPanel(){
        super();
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("点击页面,选择要抓取的Table元素"));
        this.nodes = new ArrayList<>();
    }


    public synchronized void push(DOMElementWrapper element){
        this.nodes.add(element);
    }

    public synchronized ElementBranchPanel clear(){
        this.nodes.clear();
        clickedIndex  = null ;
        return this;
    }

    private void  resetOtherStyle(){
          for (int i = 0 ; i < nodes.size() ; i++){
              int index = nodes.size() - i - 1;
               if(index != this.clickedIndex){
                   getComponent(i).setForeground(Color.BLACK);
                   nodes.get(index).resetStyle();
               }
          }
    }

    private void setClickedIndex(int index){
        this.clickedIndex = index ;
    }


    public synchronized void redraw(){
            removeAll();
            if(nodes==null ) return;
            int size = nodes.size();
            for (int i = 0; i < size; i++){
                int index = size - i - 1;
                JLabel label = new JLabel( nodes.get(index).getNodeName().concat("/"));
                label.addMouseListener(new ClickListenerImpl(this,index,i));
                add(label);
            }
            updateUI();
    }

    public DOMElement getLockedElement(){
        if(this.nodes.isEmpty()){
            return null;
        }
        return this.nodes.get(this.clickedIndex).getElement();
    }

    private static class ClickListenerImpl implements MouseListener{

        private int targetIndex;
        private int componentIndex;
        private ElementBranchPanel context;

        public ClickListenerImpl(ElementBranchPanel branchPanel ,int targetIndex,int componentIndex){
            this.targetIndex = targetIndex;
            this.context = branchPanel;
            this.componentIndex = componentIndex;
        }


        @Override
        public void mouseClicked(MouseEvent e) {
             this.context.setClickedIndex(this.targetIndex);
             this.context.getComponent(this.componentIndex).setForeground(Color.red);

             this.context.nodes.get(this.targetIndex).highlight();
             this.context.resetOtherStyle();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}
