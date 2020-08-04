package com.heaven7.android.download;

import android.net.Uri;

public class DownloadTask {

    private String url;
    private String savePath;

    private long id;

    private long totalBytes;     // init is -1
    private long downloadBytes;
    private String mediaType;    // may be null
    private String reason;
    private long lastModifyTime; // in mills
    private int status;           // download state
    private Uri localUri;

    public DownloadTask(){}
    protected DownloadTask(DownloadTask.Builder builder) {
        this.url = builder.url;
        this.savePath = builder.savePath;
        this.id = builder.id;
        this.totalBytes = builder.totalBytes;
        this.downloadBytes = builder.downloadBytes;
        this.mediaType = builder.mediaType;
        this.reason = builder.reason;
        this.lastModifyTime = builder.lastModifyTime;
        this.status = builder.status;
        this.localUri = builder.localUri;
    }

    public void setLocalUri(Uri localUri) {
        this.localUri = localUri;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public void setDownloadBytes(long downloadBytes) {
        this.downloadBytes = downloadBytes;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return this.url;
    }

    public String getSavePath() {
        return this.savePath;
    }

    public long getId() {
        return this.id;
    }

    public long getTotalBytes() {
        return this.totalBytes;
    }

    public long getDownloadBytes() {
        return this.downloadBytes;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public String getReason() {
        return this.reason;
    }

    public long getLastModifyTime() {
        return this.lastModifyTime;
    }

    public int getStatus() {
        return this.status;
    }

    public Uri getLocalUri() {
        return this.localUri;
    }

    public static class Builder {
        private String url;
        private String savePath;
        private Uri saveUri; //save uri
        private long id;
        private long totalBytes;     // init is -1
        private long downloadBytes;
        private String mediaType;    // may be null
        private String reason;
        private long lastModifyTime; // in mills
        private int status;           // download state
        private Uri localUri;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public Builder setSaveUri(Uri saveUri) {
            this.saveUri = saveUri;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setTotalBytes(long totalBytes) {
            this.totalBytes = totalBytes;
            return this;
        }

        public Builder setDownloadBytes(long downloadBytes) {
            this.downloadBytes = downloadBytes;
            return this;
        }

        public Builder setMediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder setLastModifyTime(long lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setLocalUri(Uri localUri) {
            this.localUri = localUri;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(this);
        }
    }
}
