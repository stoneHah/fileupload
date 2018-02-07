package com.zq.learn.fileuploader.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证 校验工具
 *
 * @author qun.zheng
 * @create 2018/2/7
 **/
public class IDCardUtils {
    private static int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
    private static char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值
    private static String address = "11x22x35x44x53x12x23x36x45x54x13x31x37x46x61x14x32x41x50x62x15x33x42x51x63x21x34x43x52x64x65x71x81x82x91";
    private static Pattern IDCARD_PATTERN = Pattern.compile("\\d{15}|\\d{17}[x,X,0-9]");

    /**
     * 验证是否是有效的身份证号码
     *
     * @param idCard
     * @return
     */
    public static boolean isValidIDCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return false;
        }

        boolean isIDCard = false;
        Matcher matcher = IDCARD_PATTERN.matcher(idCard);
        if (matcher.matches()) {// 可能是一个身份证
            if (idCard.length() == 18) {// 如果是18的身份证，则校验18位的身份证。15位的身份证暂不校验
                char validateCode = getValidateCode(idCard.substring(0, 17));
                if (validateCode == idCard.charAt(idCard.length() - 1)) {
                    isIDCard = true;
                }
            } else if (idCard.length() == 15) {
                isIDCard = true;
            }
        }

        return isIDCard;
    }

    private static char getValidateCode(String id17) {
        if(!StringUtils.hasText(id17) || id17.length() != 17){
            throw new IllegalArgumentException(String.format("不合法的字符长度"));
        }
        int sum = 0;
        int mode = 0;
        for (int i = 0; i < id17.length(); i++) {
            sum = sum + Integer.parseInt(String.valueOf(id17.charAt(i))) * weight[i];
        }
        mode = sum % 11;
        return validate[mode];
    }

    public static void main(String[] args) {
        System.out.println(isValidIDCard("130638200209238544"));
    }
}
