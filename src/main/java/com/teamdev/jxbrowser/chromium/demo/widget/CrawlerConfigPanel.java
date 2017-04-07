package com.teamdev.jxbrowser.chromium.demo.widget;

import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class CrawlerConfigPanel extends JPanel implements ActionListener {

    private JTextField sft = new JTextField(4);
    private JTextField slt = new JTextField(4);
    private JTextField goTo = new JTextField(20);
    private JTextField pageNo = new JTextField(4);



    private int sleep = 500 ; //抓取间隔
    private String fileName ; //抓取后生成的文件名


    //private Crawler crawler ;

    public CrawlerConfigPanel(final Crawler crawler){

     //   this.crawler = crawler ;

        setBorder(BorderFactory.createEtchedBorder());

        setLayout(new FlowLayout(FlowLayout.CENTER));
        sft.addActionListener(this);
        slt.addActionListener(this);
        JButton button = new JButton("抓取数据");
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


        add(new JLabel(",自动翻页数:"));
        add(pageNo);

        add(button);

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
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getGoToExpression(){
        return goTo.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
