package com.learn.jakarta.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name="article_id", referencedColumnName = "id")
    private Article article;

    @Column
    private Timestamp createdTimestamp;

    public Long getId() {
        return this.id;
    }
}
