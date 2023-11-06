package com.alexandervov.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Application {

    public Application(Builder builder) {
        this.name = builder.name;
        this.packageName = builder.packageName;
        this.description = builder.description;
        this.pictureName128 = builder.pictureName128;
        this.pictureName512 = builder.pictureName512;
        this.picture128Base64Bytes = builder.picture128Base64Bytes;
        this.picture512Base64Bytes = builder.picture512Base64Bytes;
        this.packageContent = builder.packageContent;
        this.category = builder.category;
        this.downloadCounter = builder.downloadCounter;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    private String packageName;

    @NotNull
    private String description;
    private String pictureName128;
    private String pictureName512;
    @Lob
    private byte[] picture128Base64Bytes;
    @Lob
    private byte[] picture512Base64Bytes;
    @Lob
    private byte[] packageContent;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Category category;
    private int downloadCounter;

    public static class Builder {
        private String name;
        private String packageName;
        private String description;
        private String pictureName128;
        private String pictureName512;
        private byte[] picture128Base64Bytes;
        private byte[] picture512Base64Bytes;
        private byte[] packageContent;
        private Category category;
        private int downloadCounter;


        public Application build() {
            return new Application(this);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder pictureName128(String pictureName128) {
            this.pictureName128 = pictureName128;
            return this;
        }

        public Builder pictureName512(String pictureName512) {
            this.pictureName512 = pictureName512;
            return this;
        }

        public Builder picture128Base64Bytes(byte[] picture128Base64Bytes) {
            this.picture128Base64Bytes = picture128Base64Bytes;
            return this;
        }

        public Builder picture512Base64Bytes(byte[] picture512Base64Bytes) {
            this.picture512Base64Bytes = picture512Base64Bytes;
            return this;
        }

        public Builder packageContent(byte[] packageContent) {
            this.packageContent = packageContent;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder downloadCounter(int downloadCounter) {
            this.downloadCounter = downloadCounter;
            return this;
        }
    }
}
