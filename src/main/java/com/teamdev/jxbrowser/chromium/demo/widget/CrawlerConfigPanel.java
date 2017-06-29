package com.teamdev.jxbrowser.chromium.demo.widget;

import com.teamdev.jxbrowser.chromium.demo.config.CrawlerConfig;
import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import com.teamdev.jxbrowser.chromium.demo.facade.CrawlerFacade;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadPool;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class CrawlerConfigPanel extends JPanel  {

    private JTextField sft = new JTextField(2);
    private JTextField slt = new JTextField(2);
    private JTextField goTo = new JTextField(30);
    private JTextField pageSetting = new JTextField(8);
    private JTextField internal = new JTextField(2);
    private JTextField fileName = new JTextField(15);
    private JButton button = new JButton("GO");

    private JRadioButton asyncType = new JRadioButton("Ajax");

    private CrawlerFacade crawler ;

    private CrawlerConfig config ;

    public CrawlerConfigPanel(){
        init();
    }

    public String validateConfig(){
        if(StringUtils.isBlank(fileName.getText())){
            return "文件名称必须填写";
        }
        if(StringUtils.isBlank(goTo.getText())){
            return "翻页js表达必须填写";
        }
        String text = pageSetting.getText();
        boolean isRight = text.matches("^\\d+-\\d+$");
        if(!isRight){
            return "起始页未正确设置,格式如:1-100";
        }
        return "";
    }

    private void init(){
        config = new CrawlerConfig();
        setBorder(BorderFactory.createTitledBorder("页面表格数据抓取配置"));

        setLayout(new FlowLayout(FlowLayout.LEADING));
        sft.setText("0");
        slt.setText("0");
        pageSetting.setText("格式:1-100");

        add(new JLabel("略过前:"));
        add(sft);
        add(new JLabel("行 , "));


        add(new JLabel("略过后:"));
        add(slt);
        add(new JLabel("行 , "));

        add(new JLabel("翻页JS:"));
        add(goTo);
        add(new JLabel(","));

        add(asyncType);
        add(new JLabel(",起始页:"));
        add(pageSetting);

        add(new JLabel("翻页间隔:"));
        internal.setText("1");
        add(internal);
        add(new JLabel("秒,"));

        add(new JLabel("文件名:"));
        add(fileName);

        add(button);

        button.addActionListener(e->{
            new Thread(()->{
                this.crawler.start();
            }).start();
        });
    }

    public void redrawCount(Integer count){
        SwingUtilities.invokeLater(()->{
            button.setText( String.format("GO(P-%s)",  count.toString()));
            button.validate();
            button.repaint();
        });
    }

    public CrawlerConfig getConfig(){
        config.setSkipFirst(Integer.valueOf(this.sft.getText()));
        config.setSkipLast(Integer.valueOf(this.slt.getText()));
        config.setGotoJs(goTo.getText());
        config.setFileName(fileName.getText());
        config.setSleepTime( (int) (Double.valueOf(internal.getText()) * 1000));
        config.setAsyn(asyncType.isSelected());

        String[] settings = pageSetting.getText().replace("格式:","").split("-");
        config.setStartPage(Integer.valueOf(settings[0]));
        config.setLastPage(Integer.valueOf(settings[1]));

        return config;
    }

    public void setCrawler(CrawlerFacade crawler) {
        this.crawler = crawler;
    }


}
