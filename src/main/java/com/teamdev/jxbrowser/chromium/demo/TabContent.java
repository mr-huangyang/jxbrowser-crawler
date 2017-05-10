/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import com.teamdev.jxbrowser.chromium.demo.crawler.TableCrawler;
import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import com.teamdev.jxbrowser.chromium.demo.facade.CrawlerFacade;
import com.teamdev.jxbrowser.chromium.demo.facade.TableCrawlerFacadeImpl;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadPool;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadUtil;
import com.teamdev.jxbrowser.chromium.demo.vo.DOMElementWrapper;
import com.teamdev.jxbrowser.chromium.demo.widget.CrawlerConfigPanel;
import com.teamdev.jxbrowser.chromium.demo.widget.ElementBranchPanel;
import com.teamdev.jxbrowser.chromium.dom.*;
import com.teamdev.jxbrowser.chromium.dom.events.DOMEventType;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * @author TeamDev Ltd.
 */
public class TabContent extends JPanel  {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabContent.class);


    private final BrowserView browserView;
    private final ToolBar toolBar;
    private final JComponent jsConsole;
    private final JComponent container;
    private final JComponent browserContainer;
    private final ElementBranchPanel branchContainer;
    private final CrawlerConfigPanel configPanel;

    private final CrawlerFacade crawlerFacade ;


    public TabContent(final BrowserView browserView) {

        this.browserView = browserView;

        branchContainer = new ElementBranchPanel();
        branchContainer.setPreferredSize(new Dimension(1000,70));
        configPanel = new CrawlerConfigPanel();
        Crawler tableCrawler = new TableCrawler(browserView.getBrowser(),branchContainer,configPanel);
        crawlerFacade = new TableCrawlerFacadeImpl(tableCrawler,browserView.getBrowser(),configPanel);
        configPanel.setCrawler(crawlerFacade);

        addBrowserLoadListener();
        browserContainer = createBrowserContainer();
        jsConsole = createConsole();
        toolBar = createToolBar(browserView);

        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        container.setPreferredSize(new Dimension(1000, 1000));

        add(toolBar);
        add(configPanel);
        add(branchContainer);
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


    private void addBrowserLoadListener() {
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
                    LOGGER.info("Main frame has failed loading: {}", errorCode);
                }
            }

            /**
             * 每次刷新页面就会重新调用
             * @param event
             */
            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent event) {

                ThreadPool.invoke(()->{
                    java.util.List<DOMNode> nodes = event.getBrowser().getDocument().getDocumentElement().getChildren();
                    nodes.forEach(t -> {
                        addOnclickListener(t);
                    });
                    boolean state = crawlerFacade.isRunning();
                    if (state) {
                        crawlerFacade.crawl();
                    }
                });

            }

            /**
             * 整个文档加载完事件,只会调用一次
             * @param event
             */
            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent event) {

            }

            private void addOnclickListener(DOMNode node) {

                TabContent tc = TabContent.this;

                node.addEventListener(DOMEventType.OnClick, domEvent -> {
                    domEvent.stopPropagation();
                    DOMElement element = (DOMElement) node;
                    trackBranch(element);
                    tc.branchContainer.redraw();

                }, false);
                java.util.List<DOMNode> children = node.getChildren();
                if (children == null || children.isEmpty()) return;
                children.forEach(n -> {
                    addOnclickListener(n);
                });
            }

        });
    }


    private void trackBranch(DOMNode element) {
        if (element == null) return;
        this.branchContainer.clear().push(new DOMElementWrapper((DOMElement) element));
        while (true) {
            DOMNode parent = element.getParent();
            if (parent == null) {
                break;
            }
            if (parent.getNodeType().ordinal() != DOMNodeType.DocumentNode.ordinal()) {
                this.branchContainer.push(new DOMElementWrapper((DOMElement) parent));
            }
            element = parent;
        }

    }


}
