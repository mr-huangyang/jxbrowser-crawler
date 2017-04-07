package com.teamdev.jxbrowser.chromium.demo.widget;

import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class CrawlerConfigPanel extends JPanel  {

    private JTextField sft = new JTextField(4);
    private JTextField slt = new JTextField(4);
    private JTextField goTo = new JTextField(20);
    private JTextField pageNo = new JTextField(4);
    private JTextField fileName = new JTextField(10);
    private int sleep = 500 ; //抓取间隔


    //private Crawler crawler ;

    public CrawlerConfigPanel(final Crawler crawler){

     //   this.crawler = crawler ;

       // setBorder(BorderFactory.createEtchedBorder());

        setBorder(BorderFactory.createTitledBorder("页面表格数据抓取配置"));

        setLayout(new FlowLayout(FlowLayout.CENTER));
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

        add(new JLabel("翻页表达式:"));
        add(goTo);


        add(new JLabel(",翻页数:"));
        add(pageNo);

        add(button);

        fileName.setText("crawler-excel");

        button.addActionListener(e->{

            // 验证  todo
            crawler.start();
        });


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
        return this.sleep ;
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

}
