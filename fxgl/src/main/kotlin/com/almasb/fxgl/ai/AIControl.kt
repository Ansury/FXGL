/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.ai.btree.BehaviorTree
import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Entity
import java.util.*

/**
 * Allows attaching a behavior tree to a game entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AIControl
private constructor() : Control() {

    private lateinit var behaviorTree: BehaviorTree<Entity>

    val bubble = AIBubble()

    /**
     * Constructs AI control with given [behaviorTree].
     */
    constructor(behaviorTree: BehaviorTree<Entity>) : this() {
        this.behaviorTree = behaviorTree
    }

    /**
     * Constructs AI control with behavior tree parsed from the asset with name [treeName].
     */
    constructor(treeName: String) : this() {

        var tree = parsedTreesCache[treeName]

        if (tree == null) {
            tree = FXGL.getAssetLoader().loadBehaviorTree(treeName)
            parsedTreesCache[treeName] = tree
        }

        this.behaviorTree = tree!!.cloneTask() as BehaviorTree<Entity>
    }

    companion object {

        private val parsedTreesCache = HashMap<String, BehaviorTree<Entity> >()
    }

    fun setBubbleMessage(message: String) {
        bubble.setMessage(message)
    }

    override fun onAdded(entity: Entity) {
        if (entity is Entity) {
            behaviorTree.`object` = entity

            if (FXGL.getSettings().applicationMode != ApplicationMode.RELEASE)
                entity.viewComponent.view.addNode(bubble)

        } else {
            throw IllegalArgumentException("Entity $entity is not Entity")
        }
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        behaviorTree.step()
    }
}