package com.vangelnum.wishes.features.download

interface Downloader {
    fun downloadFile(url: String): Long
}