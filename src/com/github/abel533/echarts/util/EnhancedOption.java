/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.abel533.echarts.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.TestConfig;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.json.GsonUtil;
import com.github.abel533.echarts.json.OptionUtil;

import utils.Constant;

/**
 * 增强的Option - 主要用于测试、演示
 *
 * @author liuzh
 */
public class EnhancedOption extends GsonOption implements TestConfig {
    private String filepath;

    /**
     * 输出到控制台
     */
    public void print() {
        GsonUtil.print(this);
    }

    /**
     * 输出到控制台
     */
    public void printPretty() {
        GsonUtil.printPretty(this);
    }

    /**
     * 在浏览器中查看
     */
    public void view() {
        if (!VIEW) {
            return;
        }
        if (this.filepath != null) {
            try {
                OptionUtil.browse(this.filepath);
            } catch (Exception e) {
                this.filepath = OptionUtil.browse(this);
            }
        } else {
            this.filepath = OptionUtil.browse(this);
        }
    }

    /**
     * 导出到指定文件名
     *
     * @param fileName
     * @return 返回html路径
     */
    
    public String exportToHtml(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return "";//exportToHtml(option, folderPath);
        }
        Writer writer = null;
        List<String> lines = readLines();
        //写入文件
        File html = new File(fileName);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(html), "UTF-8");
            for (String l : lines) {
                writer.write(l + "\n");
            }
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        //处理
        try {
            return html.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
    
    private  List<String> readLines() {
        String optionStr = GsonUtil.format(this);
        InputStream is = null;
        InputStreamReader iReader = null;
        BufferedReader bufferedReader = null;
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            iReader = new InputStreamReader(new FileInputStream(Constant.TEMPLATE), "UTF-8");
            bufferedReader = new BufferedReader(iReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("##option##")) {
                    line = line.replace("##option##", optionStr);
                }
                lines.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return lines;
    }
}
