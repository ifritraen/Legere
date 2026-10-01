package com.raen.legere.utils.canvasrecorder.pools

import android.graphics.Picture
import com.raen.legere.utils.objectpool.BaseObjectPool

class PicturePool : BaseObjectPool<Picture>(64) {

    override fun create(): Picture = Picture()

}
