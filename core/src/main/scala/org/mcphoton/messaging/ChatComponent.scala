package org.mcphoton.messaging

import java.{util => ju}

import com.electronwill.nightconfig.core.conversion.ConversionTable
import com.electronwill.nightconfig.core.{Config, UnmodifiableConfig}
import com.electronwill.nightconfig.json.MinimalJsonWriter
import org.mcphoton.messaging.Colors.Color

/**
 * @author TheElectronWill
 */
abstract class ChatComponent protected(protected[this] val data: Config) {
	final def bold(v: Boolean): this.type = {
		data.set("bold", v)
		this
	}
	final def italic(v: Boolean): this.type = {
		data.set("italic", v)
		this
	}
	final def strike(v: Boolean): this.type = {
		data.set("strikethrough", v)
		this
	}
	final def underline(v: Boolean): this.type = {
		data.set("underlined", v)
		this
	}
	final def obfusc(v: Boolean): this.type = {
		data.set("obfuscated", v)
		this
	}
	final def color(c: Color): this.type = {
		data.set("color", c.name)
		this
	}
	final def insert(txt: String): this.type = {
		data.set("insertion", txt)
		this
	}
	final def onClick(event: ChatClickEvent): this.type = {
		data.set("clickEvent", event.toConfig)
		this
	}
	final def onHover(event: ChatHoverEvent): this.type = {
		data.set("hoverEvent", event.toConfig)
		this
	}
	final def extras(components: ChatComponent*): this.type = {
		import scala.collection.JavaConverters._
		data.set("extra", components.asJava)
		this
	}
	final def +=(extra: ChatComponent): this.type = {
		val extras: ju.Collection[ChatComponent] = data.getOptional("extra")
												   .orElseGet(() => new ju.ArrayList[ChatComponent])
		extras.add(extra)
		this
	}
	override def clone(): ChatComponent = ???
	final def +(extra: ChatComponent): ChatComponent = {
		val copy: ChatComponent = this.clone()
		copy += extra
	}

	// Shortcuts
	final def bold: this.type = bold(true) // bold
	final def nbold: this.type = bold(false) // not bold

	final def italic: this.type = italic(true)
	final def nitalic: this.type = italic(false)

	final def strike: this.type = strike(true)
	final def nstrike: this.type = strike(false)

	final def underline: this.type = underline(true)
	final def nunderline: this.type = underline(false)

	final def obfusc: this.type = obfusc(true)
	final def nobfusc: this.type = obfusc(false)

	final def black: this.type = color(Colors.Black)
	final def blue: this.type = color(Colors.Blue)
	final def aqua: this.type = color(Colors.Aqua)
	final def darkBlue: this.type = color(Colors.DarkBlue)
	final def darkAqua: this.type = color(Colors.DarkAqua)
	final def darkGreen: this.type = color(Colors.DarkGreen)
	final def darkGrey: this.type = color(Colors.DarkGrey)
	final def purple: this.type = color(Colors.Purple)
	final def darkRed: this.type = color(Colors.DarkRed)
	final def gold: this.type = color(Colors.Gold)
	final def green: this.type = color(Colors.Green)
	final def grey: this.type = color(Colors.Grey)
	final def pink: this.type = color(Colors.Pink)
	final def red: this.type = color(Colors.Red)
	final def white: this.type = color(Colors.White)
	final def yellow: this.type = color(Colors.Yellow)
	final def defaultColor: this.type = color(Colors.Reset)

	final def asConfig: UnmodifiableConfig = data
	final def toJsonString: String = {
		val conversionTable = new ConversionTable()
		conversionTable.put(classOf[Color], (c: Color) => c.name)
		val converted = conversionTable.wrapRead(data)
		new MinimalJsonWriter().writeToString(converted)
	}
	final def toTermString: String = toTermString(new StringBuilder)
	final def toTermString(sb: StringBuilder): String = {
		if (data.getOptional("bold").orElse(false)) {
			sb.append("\u001B[1m")
		}
		if (data.getOptional("italic").orElse(false)) {
			sb.append("\u001B[3m")
		}
		if (data.getOptional("strikethrough").orElse(false)) {
			sb.append("\u001B[9m")
		}
		if (data.getOptional("underlined").orElse(false)) {
			sb.append("\u001B[4m")
		}
		if (data.contains("color")) {
			sb.append(data.get[Color]("color").termCode)
		}
		appendTermData(sb)
		data.getOptional[ju.Collection[ChatComponent]]("extra").ifPresent(extras => {
			import scala.collection.JavaConverters._
			for (extra: ChatComponent <- extras.asScala) {
				extra.toTermString(sb)
			}
		})
		sb.append("\u001B[0m")
		sb.mkString
	}
	protected def appendTermData(sb: StringBuilder): Unit = {}
}