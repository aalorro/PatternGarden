package com.patterngarden.ui.components

data class AvatarOption(
    val id: Int,
    val emoji: String,
    val name: String
)

val avatarList: List<AvatarOption> = listOf(
    // Flowers (10)
    AvatarOption(0, "\uD83C\uDF38", "Cherry Blossom"),
    AvatarOption(1, "\uD83C\uDF3B", "Sunflower"),
    AvatarOption(2, "\uD83C\uDF39", "Rose"),
    AvatarOption(3, "\uD83C\uDF37", "Tulip"),
    AvatarOption(4, "\uD83C\uDF3A", "Hibiscus"),
    AvatarOption(5, "\uD83C\uDF3C", "Blossom"),
    AvatarOption(6, "\uD83D\uDC90", "Bouquet"),
    AvatarOption(7, "\uD83C\uDFF5\uFE0F", "Rosette"),
    AvatarOption(8, "\uD83E\uDEB7", "Lotus"),
    AvatarOption(9, "\uD83C\uDF3E", "Sheaf of Rice"),

    // Trees & Plants (10)
    AvatarOption(10, "\uD83C\uDF3F", "Herb"),
    AvatarOption(11, "\uD83C\uDF40", "Four Leaf Clover"),
    AvatarOption(12, "\uD83C\uDF32", "Evergreen"),
    AvatarOption(13, "\uD83C\uDF33", "Deciduous Tree"),
    AvatarOption(14, "\uD83C\uDF34", "Palm Tree"),
    AvatarOption(15, "\uD83C\uDF35", "Cactus"),
    AvatarOption(16, "\uD83C\uDF84", "Christmas Tree"),
    AvatarOption(17, "\uD83C\uDF41", "Maple Leaf"),
    AvatarOption(18, "\uD83C\uDF42", "Fallen Leaf"),
    AvatarOption(19, "\uD83C\uDF43", "Leaf Fluttering"),

    // Insects (10)
    AvatarOption(20, "\uD83D\uDC1D", "Honeybee"),
    AvatarOption(21, "\uD83E\uDD8B", "Butterfly"),
    AvatarOption(22, "\uD83D\uDC1B", "Bug"),
    AvatarOption(23, "\uD83D\uDC1E", "Ladybug"),
    AvatarOption(24, "\uD83D\uDC1C", "Ant"),
    AvatarOption(25, "\uD83E\uDEB2", "Beetle"),
    AvatarOption(26, "\uD83D\uDC0C", "Snail"),
    AvatarOption(27, "\uD83E\uDD97", "Cricket"),
    AvatarOption(28, "\uD83E\uDEB0", "Fly"),
    AvatarOption(29, "\uD83E\uDD82", "Scorpion"),

    // Birds (5)
    AvatarOption(30, "\uD83D\uDC26", "Bird"),
    AvatarOption(31, "\uD83E\uDD9C", "Parrot"),
    AvatarOption(32, "\uD83D\uDC24", "Baby Chick"),
    AvatarOption(33, "\uD83E\uDD86", "Duck"),
    AvatarOption(34, "\uD83E\uDD89", "Owl"),

    // Fruits (5)
    AvatarOption(35, "\uD83C\uDF4E", "Apple"),
    AvatarOption(36, "\uD83C\uDF4A", "Orange"),
    AvatarOption(37, "\uD83C\uDF4B", "Lemon"),
    AvatarOption(38, "\uD83C\uDF47", "Grapes"),
    AvatarOption(39, "\uD83C\uDF53", "Strawberry"),

    // Garden & Nature (5)
    AvatarOption(40, "\uD83E\uDEB4", "Potted Plant"),
    AvatarOption(41, "\uD83C\uDF44", "Mushroom"),
    AvatarOption(42, "\uD83E\uDDFA", "Basket"),
    AvatarOption(43, "\u2618\uFE0F", "Shamrock"),
    AvatarOption(44, "\uD83C\uDF1F", "Glowing Star"),

    // Weather & Sky (5)
    AvatarOption(45, "\u2600\uFE0F", "Sun"),
    AvatarOption(46, "\uD83C\uDF24\uFE0F", "Sun Behind Cloud"),
    AvatarOption(47, "\uD83C\uDF08", "Rainbow"),
    AvatarOption(48, "\u2744\uFE0F", "Snowflake"),
    AvatarOption(49, "\uD83C\uDF19", "Crescent Moon")
)

fun getAvatar(id: Int): AvatarOption {
    return avatarList.getOrElse(id) { avatarList[0] }
}
