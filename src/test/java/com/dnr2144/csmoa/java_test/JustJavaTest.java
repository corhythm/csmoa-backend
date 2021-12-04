package com.dnr2144.csmoa.java_test;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class JustJavaTest {

    @Test
    void StringSplitTest() {
        String urls = "http://www.7-eleven.co.kr/upload/product/8801007/178233.1.jpg,http://bgf-cu.xcache.kinxcdn.com/product/8801051158496.jpg,http://bgf-cu.xcache.kinxcdn.com/product/8801038570303.jpg,http://gs25appimg.gsretail.com/imgsvr/item/GD_8801062873333_001.jpg,http://www.7-eleven.co.kr/upload/product/4005808/372881.1.jpg";

        List<String> reviewUrls = Arrays.asList(urls.split(",", -1));
        for (String reviewUrl : reviewUrls) {
            System.out.println(reviewUrl);
        }
    }
}
