package com.nimo.fb_effect.model;

import android.util.Log;
import com.nimo.fb_effect.model.FBBeautyFilterConfig.FBBeautyFilter;
//import com.nimo.fb_effect.model.HtEffectFilterConfig.HtEffectFilter;
//import com.nimo.fb_effect.model.HtFunnyFilterConfig.HtFunnyFilter;
//import com.nimo.fb_effect.model.HtHairConfig.HtHair;
//import com.nimo.fb_effect.model.HtMakeupStyleConfig.HtMakeupStyle;
import com.nimo.fb_effect.utils.FBUICacheUtils;

/**
 * UI状态容器
 */
public class FBState {

  //一级面板是哪个
  public static FBViewState currentViewState = FBViewState.HIDE;

  //二级面板是哪个
  public static FBViewState currentSecondViewState = FBViewState.BEAUTY_SKIN;

  public static FBViewState currentSliderViewState = FBViewState.BEAUTY_SKIN;

  //是否处于第三面板
//  public static FBViewState currentThirdViewState = FBViewState.MAKEUP_OUT;

  // 当前选中了哪个美颜参数
  private static FBBeautyKey currentBeautySkin = FBBeautyKey.NONE;

  // 当前选中了哪个美型参数
  public static FBFaceTrim currentFaceTrim = FBFaceTrim.EYE_ENLARGING;

  // 当前选中了哪个美妆参数
//  public static HtMakeUpEnum currentMakeUp = HtMakeUpEnum.LIPSTICK;

//  public static HtMakeup currentLipstick = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentEyebrow = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentBlush = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentEyeshadow = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentEyeline = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentEyelash = HtMakeup.NO_MAKEUP;
//  public static HtMakeup currentPupils = HtMakeup.NO_MAKEUP;



  // 当前选中了哪个妆容推荐参数
//  public static HtMakeupStyle currentMakeUpStyle = HtMakeupStyle.NO_STYLE;
  // public static HtMakeupStyleConfig currentMakeUpStyle = HtMakeupStyleConfig.NONE;

  // 当前选中了哪个美体参数
//  public static HtBody currentBody = HtBody.LONG_LEG;

  // 当前选中了哪个滤镜
  public static FBBeautyFilter currentStyleFilter = FBBeautyFilter.NO_FILTER;
//  public static HtEffectFilter currentEffectFilter = HtEffectFilter.NO_FILTER;
//  public static HtFunnyFilter currentFunnyFilter = HtFunnyFilter.NO_FILTER;

  // 当前选中了哪个美发
//  public static HtHair currentHair = HtHair.NO_HAIR;

  // 选中哪个风格
  // public static HtStyle currentStyle = HtStyle.YUAN_TU;
//  public static HtMakeupStyle currentStyle = HtMakeupStyle.NO_STYLE;

  // 当前选中哪个AR道具
//  public static FBViewState currentAR = FBViewState.AR_PROP;

  // 当前选中哪个妆容推荐
  //public static HtMakeup currentMakeup = HtMakeup.NONE;



  //释放黑色主题
  public static boolean isDark = true;

  public static FBBeautyKey getCurrentBeautySkin() {
    return currentBeautySkin;
  }

  public static void setCurrentBeautySkin(FBBeautyKey currentBeautySkin) {
    Log.e("设置当前的美肤",currentBeautySkin.name());
    FBState.currentBeautySkin = currentBeautySkin;
  }

  //释放
  public static void release() {
    currentViewState = FBViewState.HIDE;
    currentSecondViewState = FBViewState.BEAUTY_SKIN;
    currentBeautySkin = FBBeautyKey.NONE;
    currentFaceTrim = FBFaceTrim.EYE_ENLARGING;

    FBUICacheUtils.beautyFaceTrimPosition(-1);
    FBUICacheUtils.beautySkinPosition(-1);
//    FBUICacheUtils.beautyStylePosition(0);
//    FBUICacheUtils.setBeautyMakeUpStylePosition(0);
//    FBUICacheUtils.beautyBodyPosition(0);
  }
}
