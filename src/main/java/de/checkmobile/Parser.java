package de.checkmobile;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by icetusk on 18.07.16.
 */
public class Parser {
    public static void main(String[] args) throws Exception{

        File carFile = new File("/home/icetusk/Desktop/car_converted_small.svg");

        SAXReader reader = new SAXReader();
        Document document = reader.read(carFile);

        List<Element> paths = document.getRootElement().elements("path");

        System.out.println(paths.size());

        if(true) return;

        Element e = document.getRootElement().element("style");

        //System.out.println(e.getText());


        String stylesText = e.getText();

        Pattern p = Pattern.compile("[.](.*?)[{](.*?)[}]");

        Matcher m = p.matcher(stylesText);


        Map<String, Map<String, String>> styles = new HashMap<>();

        while(m.find()) {
            String styleClassName = m.group(1);
            String styleBody = m.group(2);

            Map<String, String> styleValues = new HashMap<>();

            //System.out.println(styleBody);

            Pattern p2 = Pattern.compile("(.*?)[:](.*?)[;]");
            Matcher m2 = p2.matcher(styleBody);

            while(m2.find()) {
                String styleName = m2.group(1).trim();
                String styleValue = m2.group(2).trim();
                //System.out.println(styleName + ":::" + styleValue);
                styleValues.put(styleName, styleValue);
            }
            styles.put(styleClassName, styleValues);
        }

        System.out.println("... Styles init done!");



        System.out.println("PATHS: "+paths.size());

        for(Element z: paths) {

            Attribute a = z.attribute("class");

            if(a!=null) {
                String[] classes = a.getText().split(" ");

                String styleName = classes[0];

                Map<String, String> styleValues = styles.get(styleName);

                for(String k: styleValues.keySet()) {
                    String v = styleValues.get(k);
                    z.addAttribute(k,v);
                }
            }

        }

        List<Element> lines = document.getRootElement().elements("line");

        System.out.println(paths.size() + "+" +lines.size());

        SAXWriter sw = new SAXWriter();

        // lets write to a file
        XMLWriter writer = new XMLWriter(
                new FileWriter( "/home/icetusk/Desktop/car_converted.svg" )
        );
        writer.write( document );
        writer.close();
    }
}
