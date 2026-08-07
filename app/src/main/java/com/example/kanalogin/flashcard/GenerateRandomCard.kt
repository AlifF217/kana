package com.example.kanalogin.flashcard

import com.example.kanalogin.R

fun generateRandomCard(): Flashcard {
    val items = listOf(
        Flashcard(R.drawable.apple, "リンゴ\n(Ringo)", listOf("リンゴ\n(Ringo)", "みず\n(Mizu)", "くるま\n(Kuruma)", "ねこ\n(Neko)").shuffled()),
        Flashcard(R.drawable.water, "みず\n(Mizu)", listOf("みず\n(Mizu)", "いぬ\n(Inu)", "とり\n(Tori)", "はな\n(Hana)").shuffled()),
        Flashcard(R.drawable.car, "くるま\n(Kuruma)", listOf("くるま\n(Kuruma)", "じてんしゃ\n(Jitensha)", "バス\n(Basu)", "ひこうき\n(Hikouki)").shuffled()),
        Flashcard(R.drawable.cat, "ねこ\n(Neko)", listOf("ねこ\n(Neko)", "いぬ\n(Inu)", "さる\n(Saru)", "とり\n(Tori)").shuffled()),
        Flashcard(R.drawable.dog, "いぬ (Inu)", listOf("いぬ (Inu)", "さる (Saru)", "とり (Tori)", "みず (Mizu)").shuffled()),
        Flashcard(R.drawable.bird, "とり (Tori)", listOf("とり (Tori)", "ねこ (Neko)", "バス (Basu)", "ひこうき (Hikouki)").shuffled()),
        Flashcard(R.drawable.flower, "はな (Hana)", listOf("はな (Hana)", "りんご (Ringo)", "くるま (Kuruma)", "じてんしゃ (Jitensha)").shuffled()),
        Flashcard(R.drawable.bicycle, "じてんしゃ (Jitensha)", listOf("じてんしゃ (Jitensha)", "くるま (Kuruma)", "バス (Basu)", "ひこうき (Hikouki)").shuffled()),
        Flashcard(R.drawable.bus, "バス (Basu)", listOf("バス (Basu)", "ひこうき (Hikouki)", "くるま (Kuruma)", "じてんしゃ (Jitensha)").shuffled()),
        Flashcard(R.drawable.airplane, "ひこうき (Hikouki)", listOf("ひこうき (Hikouki)", "バス (Basu)", "くるま (Kuruma)", "ねこ (Neko)").shuffled()),
        Flashcard(R.drawable.monkey, "さる (Saru)", listOf("さる (Saru)", "いぬ (Inu)", "ねこ (Neko)", "とり (Tori)").shuffled()),
        Flashcard(R.drawable.book, "ほん (Hon)", listOf("ほん (Hon)", "くるま (Kuruma)", "ねこ (Neko)", "いぬ (Inu)").shuffled()),
        Flashcard(R.drawable.house, "いえ (Ie)", listOf("いえ (Ie)", "みず (Mizu)", "りんご (Ringo)", "バス (Basu)").shuffled()),
        Flashcard(R.drawable.phone, "でんわ (Denwa)", listOf("でんわ (Denwa)", "ほん (Hon)", "スマートフォン (Sumātofon)", "いぬ (Inu)").shuffled()),
        Flashcard(R.drawable.smartphone, "スマートフォン (Sumātofon)", listOf("スマートフォン (Sumātofon)", "でんわ (Denwa)", "くるま (Kuruma)", "いぬ (Inu)").shuffled()),
        Flashcard(R.drawable.tree, "き (Ki)", listOf("き (Ki)", "はな (Hana)", "ねこ (Neko)", "いぬ (Inu)").shuffled()),
        //Flashcard(R.drawable.shoe, "くつ (Kutsu)", listOf("くつ (Kutsu)", "ほん (Hon)", "くるま (Kuruma)", "じてんしゃ (Jitensha)").shuffled()),
        //Flashcard(R.drawable.chair, "いす (Isu)", listOf("いす (Isu)", "くつ (Kutsu)", "ほん (Hon)", "ねこ (Neko)").shuffled()),
        //Flashcard(R.drawable.table, "つくえ (Tsukue)", listOf("つくえ (Tsukue)", "いす (Isu)", "ほん (Hon)", "くつ (Kutsu)").shuffled()),
        //Flashcard(R.drawable.clock, "とけい (Tokei)", listOf("とけい (Tokei)", "いえ (Ie)", "でんわ (Denwa)", "き (Ki)").shuffled()),
        //Flashcard(R.drawable.sun, "たいよう (Taiyou)", listOf("たいよう (Taiyou)", "つき (Tsuki)", "き (Ki)", "はな (Hana)").shuffled())
    )
    return items.random()
}
