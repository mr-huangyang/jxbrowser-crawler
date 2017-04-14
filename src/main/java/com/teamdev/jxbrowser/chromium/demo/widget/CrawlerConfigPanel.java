package com.teamdev.jxbrowser.chromium.demo.widget;

import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class CrawlerConfigPanel extends JPanel  {

    private static  final  String TIP_PATTERN = "已抓取第%s页";

    private JTextField sft = new JTextField(4);
    private JTextField slt = new JTextField(4);
    private JTextField goTo = new JTextField(15);
    private JTextField pageNo = new JTextField(4);
    private JTextField internal = new JTextField(4);
    private JTextField fileName = new JTextField(20);
    private JLabel countLabel = new JLabel(String.format(TIP_PATTERN,0));

    private JRadioButton asyncType = new JRadioButton("Ajax");

    private Crawler crawler ;

    public CrawlerConfigPanel(){
        init();
    }

    public String validateConfig(){
        if(StringUtils.isBlank(fileName.getText())){
            return "文件名称必须填写";
        }
        return "";
    }

    private void init(){
        setBorder(BorderFactory.createTitledBorder("页面表格数据抓取配置"));

        setLayout(new FlowLayout(FlowLayout.LEADING));
        JButton button = new JButton("抓取Table数据");
        sft.setText("0");
        slt.setText("0");
        pageNo.setText("0");

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
        add(new JLabel(",翻页数:"));
        add(pageNo);

        add(new JLabel("翻页间隔:"));
        internal.setText("1");
        add(internal);
        add(new JLabel("秒,"));

        add(new JLabel("文件名称:"));
        add(fileName);

        add(button);
        add(countLabel);
        countLabel.setBorder(BorderFactory.createEtchedBorder());

        button.addActionListener(e->{
            new Thread(()->{
                this.crawler.start();
            }).start();
        });
    }

    public void redrawCount(Integer count){
        SwingUtilities.invokeLater(()->{
            countLabel.setText( String.format(TIP_PATTERN,  count.toString()));
            countLabel.validate();
            countLabel.repaint();
        });
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    public int getSkipFirst(){
        return Integer.valueOf(this.sft.getText());
    }

    public int getSkipLast(){
        return Integer.valueOf(this.slt.getText());
    }


    public int getPageNo(){
        return Integer.valueOf(this.pageNo.getText());
    }


    public int getSleep(){
        return Integer.valueOf(internal.getText()) * 1000 ;
    }

    public String getFileName() {
        return this.fileName.getText();
    }

    public void setFileName(String fileName) {
        this.fileName.setText(fileName);
    }
    public String getGoToExpression(){
        return goTo.getText();
    }

    public boolean isAsyncPage() {
        return asyncType.isSelected();
    }
}
