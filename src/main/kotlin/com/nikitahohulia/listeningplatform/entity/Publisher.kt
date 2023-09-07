package com.nikitahohulia.listeningplatform.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "publisher")
class Publisher(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true, nullable = false)
    var publisherName: String,
    @OneToMany
    val posts: MutableSet<Post> = mutableSetOf(),
    @Column(nullable = true)
    var rating: Double? = null
)
