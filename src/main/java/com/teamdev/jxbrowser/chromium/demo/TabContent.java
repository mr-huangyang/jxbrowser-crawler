/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadUtil;
import com.teamdev.jxbrowser.chromium.demo.util.XpathUtility;
import com.teamdev.jxbrowser.chromium.demo.vo.DOMElementWrapper;
import com.teamdev.jxbrowser.chromium.demo.widget.CrawlerConfigPanel;
import com.teamdev.jxbrowser.chromium.demo.widget.ElementBranchPanel;
import com.teamdev.jxbrowser.chromium.dom.*;
import com.teamdev.jxbrowser.chromium.dom.events.DOMEventType;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TeamDev Ltd.
 */
public class TabContent extends JPanel implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabContent.class);


    private final BrowserView browserView;
    private final ToolBar toolBar;
    private final JComponent jsConsole;
    private final JComponent container;
    private final JComponent browserContainer;
    private final ElementBranchPanel branchContainer  ;
    private final CrawlerConfigPanel configPanel ;


    private boolean headExistFlag = false;
    private boolean running = false ;
    private int counting;

    private boolean block = true ;



    public TabContent(final BrowserView browserView) {

        this.browserView = browserView;
        branchContainer = new ElementBranchPanel();
        configPanel = new CrawlerConfigPanel(this);

        addBrowserLoadListener();
        browserContainer = createBrowserContainer();
        jsConsole = createConsole();
        toolBar = createToolBar(browserView);

        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        container.setPreferredSize(new Dimension(1000,1000));

        add(branchContainer);
        add(configPanel);
        add(toolBar);
        add(container);
    }


    private ToolBar createToolBar(BrowserView browserView) {
        ToolBar toolBar = new ToolBar(browserView);
        toolBar.addPropertyChangeListener("TabClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("TabClosed", false, true);
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleDisplayed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                showConsole();
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
            }
        });
        return toolBar;
    }

    private void hideConsole() {
        showComponent(browserContainer);
    }

    private void showComponent(JComponent component) {
        container.removeAll();
        container.add(component, BorderLayout.CENTER);
        validate();
    }

    private void showConsole() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(browserContainer, JSplitPane.TOP);
        splitPane.add(jsConsole, JSplitPane.BOTTOM);
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        showComponent(splitPane);
    }

    private JComponent createConsole() {
        JSConsole result = new JSConsole(browserView.getBrowser());
        result.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
                toolBar.didJSConsoleClose();
            }
        });
        return result;
    }

    private JComponent createBrowserContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(browserView, BorderLayout.CENTER);
        return container;
    }

    public void dispose() {
        browserView.getBrowser().dispose();
    }


    private  void addBrowserLoadListener(){
        browserView.getBrowser().addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()) {
                    LOGGER.info("Main frame has started loading");
                }
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                if (event.isMainFrame()) {
                    LOGGER.info("Provisional load was committed for a frame");
                }
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    firePropertyChange("PageTitleChanged", null, TabContent.this.browserView.getBrowser().getTitle());
                    LOGGER.info("Main frame has finished loading");
                }

            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent event) {
                NetError errorCode = event.getErrorCode();
                if (event.isMainFrame()) {
                    LOGGER.info("Main frame has failed loading: {}" ,errorCode);
                }
            }

            /**
             * 每次刷新页面就会重新调用
             * @param event
             */
            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent event) {
                // branchContainer.clear();//清除
                java.util.List<DOMNode> nodes = event.getBrowser().getDocument().getDocumentElement().getChildren();
                nodes.forEach(t->{
                    addOnclickListener(t);
                });

                boolean state = isRunning();
                if(state){
                    doCrawler();
                    block = false ;
                }
            }

            /**
             * 整个文档加载完事件,只会调用一次
             * @param event
             */
            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent event) {

            }

            private void addOnclickListener(DOMNode node){

                TabContent tc = TabContent.this;

                node.addEventListener(DOMEventType.OnClick , domEvent -> {
                    domEvent.stopPropagation();
                    DOMElement element = (DOMElement) node ;
                    trackBranch(element);
                    tc.branchContainer.redraw();

                }, false);
                java.util.List<DOMNode> children = node.getChildren();
                if(children==null||children.isEmpty()) return ;
                children.forEach(n->{
                    addOnclickListener(n);
                });
            }

        });
    }



    private  void trackBranch(DOMNode element){
        if(element == null) return;
        this.branchContainer.clear().push(new DOMElementWrapper((DOMElement) element));
        while (true){
            DOMNode parent =  element.getParent();
            if(parent==null ){
                break;
            }
            if(parent.getNodeType().ordinal() != DOMNodeType.DocumentNode.ordinal()){
                this.branchContainer.push(new DOMElementWrapper((DOMElement) parent));
            }
            element =  parent ;
        }

    }

    @Override
    public void start() {
        this.running = true ;

        //首先获取目标table
        doCrawler();

        setTableHeadExist(true);
        int pageNo = configPanel.getPageNo();
        String goToExpression = configPanel.getGoToExpression();

        for (int j = 0  ; j < pageNo  ; j++ ){
                this.browserView.getBrowser().executeJavaScript(goToExpression.replace("$1",(j+2)+""));
                while (block){
                    ThreadUtil.sleep(this.configPanel.getSleep());
                }
                block = true ;
        }
    }


    private void doCrawler(){
        DOMElement element = this.branchContainer.getLockedElement();
        String table = XpathUtility.xpath(element);

        DOMDocument document = this.browserView.getBrowser().getDocument();
        DOMElement dataElement = document.findElement(By.xpath(table));
        java.util.List<DOMElement> tr = dataElement.findElements(By.tagName("tr"));
        List<List<String>> rows = new ArrayList<>();

        int skipFirst = this.configPanel.getSkipFirst();
        int skipLast = this.configPanel.getSkipLast();
        int size = tr.size() - (skipLast == 0 ? 0 : skipLast);
        for (int i = (skipFirst==0?0:skipFirst); i < size; i++){

            List<DOMElement> tds = tr.get(i).findElements(By.tagName("td"));
            List<String> cells = new ArrayList<>();
            tds.forEach(ele->{
                cells.add(ele.getInnerText());
            });
            if(! cells.isEmpty()){
                rows.add(cells);
            }
        }
        if(isTableHeadExist() && rows.size() > 0){
            rows.remove(0);
        }
        Excel.getFile(this.configPanel.getFileName() + ".xlsx").save(rows);

        this.count();
        LOGGER.info("已抓取第[{}]页",this.getCount());
    }


    @Override
    public void count() {
        this.counting++;
    }

    @Override
    public int getCount() {
        return counting ;
    }

    @Override
    public boolean isRunning() {
          return this.running;
    }

    @Override
    public void stop() {
        this.running = false ;
    }

    @Override
    public boolean isTableHeadExist() {
        return this.headExistFlag;
    }

    @Override
    public void setTableHeadExist(boolean flag) {
        this.headExistFlag = flag ;
    }
}
