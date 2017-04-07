/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.dom.DOMNode;
import com.teamdev.jxbrowser.chromium.dom.events.DOMEventType;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;

/**
 * @author TeamDev Ltd.
 */
public class JxBrowserDemo {

    private static String getElementPath(DOMNode element){
        if(element == null) return "";
        System.out.println(element.getNodeName());
        Stack<DOMNode> eles = new Stack<>();
        eles.push(element);
        while (true){
            DOMNode parent = element.getParent();
            if(parent==null){
                eles.pop();//把document元素移除
                StringBuilder sb = new StringBuilder();
                while (!eles.isEmpty()){
                    sb.append(eles.pop().getNodeName()).append("/");
                }
                return sb.toString();
            }
            eles.push(parent);
            element =  parent ;
        }

    }

    public static void main(String[] args) {
        final Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JButton button = new JButton("Get Selected HTML");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String html = browser.getSelectedHTML();
                JOptionPane.showMessageDialog(view,
                        html, "Selected HTML", JOptionPane.PLAIN_MESSAGE);
            }
        });


        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()) {
                    System.out.println("Main frame has started loading");
                }
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                if (event.isMainFrame()) {
                    System.out.println("Provisional load was committed for a frame");
                }
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    System.out.println("Main frame has finished loading");
                }
            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent event) {
                NetError errorCode = event.getErrorCode();
                if (event.isMainFrame()) {
                    System.out.println("Main frame has failed loading: " + errorCode);
                }
            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent event) {
                System.out.println("Frame document is loaded.");
            }

            /**
             * 整个文档加载完事件
             * @param event
             */
            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent event) {
                List<DOMElement> table = event.getBrowser().getDocument().findElements(By.cssSelector("*"));
                table.forEach(t->{

                    t.addEventListener(DOMEventType.OnClick , domEvent -> {
                        domEvent.stopPropagation();
                        String elementPath = JxBrowserDemo.getElementPath(t);
                        JOptionPane.showMessageDialog(view, elementPath
                                , "点中了我", JOptionPane.PLAIN_MESSAGE);
                    },false);
                });
            }
        });

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(button, BorderLayout.NORTH);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        String url = "http://www.kmybzc.com/yb/ybindex.html";
        browser.loadURL(url);
    }

}
