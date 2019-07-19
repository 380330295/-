package com.xuecheng.search.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Kiku
 * @date 2019/7/9 16:44
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(CourseSearchProperties.class)
public class SearchPropertiesTest {

    @Autowired
    CourseSearchProperties searchProperties;

    @Test
    public void testSearchProperties() {
        System.out.println(searchProperties.getIndex());
        System.out.println(searchProperties.getType());
        String s = Arrays.toString(searchProperties.getSourceField());
        System.out.println(s);
    }
}