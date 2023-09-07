package com.nikitahohulia.listeningplatform.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true, nullable = false)
    var username: String,
    @Column(nullable = false)
    var password: String,
    @OneToMany
    @JoinColumn(name = "subscription_id")
    var subscriptions: MutableSet<Subscription> = mutableSetOf(),
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "publisher_id")
    var publisher: Publisher? = null
)
