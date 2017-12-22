package com.example.mirry.recognizedemo.service;

import com.example.mirry.recognizedemo.utils.Base64Util;
import com.example.mirry.recognizedemo.utils.FileUtil;
import com.example.mirry.recognizedemo.utils.HttpUtil;

import java.net.URLEncoder;

/**
 * Created by Mirry on 2017/12/11.
 */

public class OCRService {
    public static String getRecognizeResult(String filePath){
        // 通用识别url
        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";

        try {
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String params = "detect_direction=" + true + "&" +
                    URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
            /*
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(otherHost, accessToken, params);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
