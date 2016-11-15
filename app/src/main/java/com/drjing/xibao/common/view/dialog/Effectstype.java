package com.drjing.xibao.common.view.dialog;

import com.drjing.xibao.common.view.dialog.effects.BaseEffects;
import com.drjing.xibao.common.view.dialog.effects.FadeIn;
import com.drjing.xibao.common.view.dialog.effects.FlipH;
import com.drjing.xibao.common.view.dialog.effects.FlipV;
import com.drjing.xibao.common.view.dialog.effects.NewsPaper;
import com.drjing.xibao.common.view.dialog.effects.SideFall;
import com.drjing.xibao.common.view.dialog.effects.SlideBottom;
import com.drjing.xibao.common.view.dialog.effects.SlideLeft;
import com.drjing.xibao.common.view.dialog.effects.SlideRight;
import com.drjing.xibao.common.view.dialog.effects.SlideTop;
import com.drjing.xibao.common.view.dialog.effects.Fall;
import com.drjing.xibao.common.view.dialog.effects.RotateLeft;
import com.drjing.xibao.common.view.dialog.effects.RotateBottom;
import com.drjing.xibao.common.view.dialog.effects.Shake;
import com.drjing.xibao.common.view.dialog.effects.Slit;

/**
 * Created by kristain on 16/1/21.
 */
public enum Effectstype {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(RotateBottom.class),
    RotateLeft(RotateLeft.class),
    Slit(Slit.class),
    Shake(Shake.class),
    Sidefill(SideFall.class);
    private Class<? extends BaseEffects> effectsClazz;

    private Effectstype(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects=null;
        try {
            bEffects = effectsClazz.newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }
}
