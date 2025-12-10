package org.roldy.rendering.pawn.skeleton.slot

import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.Attachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import org.roldy.rendering.pawn.skeleton.attribute.PawnSkeletonSlot
import kotlin.properties.Delegates

abstract class PawnSlotData<T : PawnSkeletonSlot>(
    val slot: Slot,
    val slotName: T,
    private val originAttachment: RegionAttachment
) {
    var currentAttachment: Attachment? by Delegates.observable(originAttachment) { _, _, newValue ->
        slot.attachment = newValue
    }

    fun remove() {
        currentAttachment = null
    }

    fun update(attachment: RegionAttachment) {
        attachment.x = originAttachment.x
        attachment.y = originAttachment.y
        attachment.rotation = originAttachment.rotation
        attachment.scaleX = originAttachment.scaleX
        attachment.scaleY = originAttachment.scaleY
        attachment.height = originAttachment.height
        attachment.width = originAttachment.width
        attachment.color.set(originAttachment.color)
        attachment.updateRegion()
        this.currentAttachment = attachment
    }
}
