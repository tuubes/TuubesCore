package org.mcphoton.event

import java.time.Instant

/**
 * @author TheElectronWill
 */
abstract class Event(val instant: Instant = Instant.now) {
}