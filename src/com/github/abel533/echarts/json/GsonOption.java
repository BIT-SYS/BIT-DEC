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

package com.github.abel533.echarts.json;

import com.github.abel533.echarts.Option;

/**
 * 澧炲己鐨凮ption - 涓昏鐢ㄤ簬娴嬭瘯銆佹紨绀�
 *
 * @author liuzh
 */
public class GsonOption extends Option {

    /**
     * 鍦ㄦ祻瑙堝櫒涓煡鐪�
     */
    public void view() {
        OptionUtil.browse(this);
    }

    @Override
    /**
     * 鑾峰彇toString鍊�
     */
    public String toString() {
        return GsonUtil.format(this);
    }

    /**
     * 鑾峰彇toPrettyString鍊�
     */
    public String toPrettyString() {
        return GsonUtil.prettyFormat(this);
    }

    /**
     * 瀵煎嚭鍒版寚瀹氭枃浠跺悕
     *
     * @param fileName
     * @return 杩斿洖html璺緞
     */
    public String exportToHtml(String fileName) {
        return exportToHtml(System.getProperty("java.io.tmpdir"), fileName);
    }

    /**
     * 瀵煎嚭鍒版寚瀹氭枃浠跺悕
     *
     * @param fileName
     * @return 杩斿洖html璺緞
     */
    public String exportToHtml(String filePath, String fileName) {
        return OptionUtil.exportToHtml(this, filePath, fileName);
    }

}
