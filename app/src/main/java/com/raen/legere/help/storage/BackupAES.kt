package com.raen.legere.help.storage

import cn.hutool.crypto.symmetric.AES
import com.raen.legere.help.config.LocalConfig
import com.raen.legere.utils.MD5Utils

class BackupAES : AES(
    MD5Utils.md5Encode(LocalConfig.password ?: "").encodeToByteArray(0, 16)
)