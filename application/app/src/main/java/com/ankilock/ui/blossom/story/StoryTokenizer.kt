package com.ankilock.ui.blossom.story
    
import com.ankilock.data.StoryWordItem
    
data class RubySegment( 
    val text: String, 
    val ruby: String? = null 
) 
    
data class StoryToken( 
    val surface: String, 
    val segments: List<RubySegment>, 
    val isName: Boolean = false, 
    val isPunctuation: Boolean = false, 
    val isTarget: Boolean = false, 
    val meaning: String = "" 
) 
    
object StoryTokenizer { 
    
    private val characterNames = setOf( 
        "マコト", "マリ", "タロウ", "ユキ", "マット", 
        "ケンジ", "ソラ", "リン", "アオイ", "ダイキ", "シン", "ローガン" 
    ) 
    
    private val commonParticles = listOf( 
        "から", "まで", "より", "たり", "たら", 
        "は", "が", "を", "に", "へ", "で", "と", "も", "の", "ね", "よ", "か", "て" 
    ) 
    
    private val commonAuxiliaries = listOf( 
        "ていませんでした", "ていません", "ていました", "ています", 
        "ていた", "ている", "てある", "でした", "ません", "ました", 
        "である", 
        "たいです", "たかった", "ないです", "なかった", 
        "です", "ます", "たい", "ない", "だ" 
    ) 
    
    private val commonHiraganaPhrases = listOf( 
        "仕事をしています", "仕事をしている", 
        "しています", "していました", "していません", "していませんでした", 
        "している", "していた", "しないで", "しない", 
        "します", "しました", "しますか", "しましたか", 
        "させて", "される", "された", "されます", 
        "はじめました", "はじめた", "はじめます", "はじめる", 
        "おわりました", "おわった", "おわります", "おわる", 
        "たくさん", "とても", "いつも", "ときどき", 
        "いっしょに", "ゆっくり", "そろそろ", "やっぱり", 
        "だんだん", "どんどん", "ますます", "ちょっと", 
        "みんなで", "みんな", "どこか", "なにか", 
        "だれか", "いつか", "どうして", "なぜ", 
        "そして", "それから", "だから", "ですから", 
        "しかし", "けれども", "けれど", "でも", "また" 
    ) 
    
    val commonWordMeanings = mapOf( 
        "会いました" to "met / saw (a friend)", 
        "会う" to "to meet / see (someone)", 
        "会います" to "meet / will meet", 
        "会って" to "meeting / meet and...", 
        "会った" to "met", 
        "仕事" to "work / job", 
        "仕事をしています" to "is working / doing work", 
        "しています" to "is doing / doing", 
        "していました" to "was doing", 
        "します" to "will do / does", 
        "しました" to "did", 
        "友達" to "friend", 
        "公園" to "park", 
        "晴れた" to "sunny / clear", 
        "土曜日" to "Saturday", 
        "午後" to "afternoon", 
        "かくれんぼ" to "hide and seek", 
        "提案" to "suggestion / proposal", 
        "提案しました" to "suggested / proposed", 
        "隠れました" to "hid / concealed oneself", 
        "隠れる" to "to hide", 
        "笑顔" to "smile", 
        "見つけました" to "found / discovered", 
        "叫んで" to "shouting / called out", 
        "大笑い" to "hearty laugh / big laugh", 
        "大笑いしました" to "laughed out loud", 
        "学びながら" to "while learning", 
        "仲良く" to "happily together / getting along well", 
        "遊びました" to "played", 
        "遊ぶ" to "to play", 
        "学校" to "school", 
        "行く" to "to go", 
        "行きました" to "went", 
        "食べる" to "to eat", 
        "食べました" to "ate", 
        "見る" to "to see / look", 
        "見えます" to "can be seen", 
        "帰る" to "to return home", 
        "帰って" to "returning home", 
        "料理" to "cooking", 
        "始める" to "to start", 
        "買う" to "to buy", 
        "買いました" to "bought", 
        "お金" to "money", 
        "払う" to "to pay", 
        "払って" to "paying", 
        "袋" to "bag", 
        "家族" to "family", 
        "家" to "house / home", 
        "靴" to "shoes", 
        "玄関" to "entrance hall", 
        "庭" to "garden", 
        "部屋" to "room", 
        "桜" to "cherry blossom", 
        "木" to "tree", 
        "陰" to "shade / shadow", 
        "静かに" to "quietly", 
        "鬼" to "the person who is 'it'", 
        "暮れる" to "to grow dark / sun sets", 
        "チームワーク" to "teamwork" 
    ) 
    
    val commonFuriganaMap = mapOf( 
        "会いました" to "会[あ]いました", 
        "会います" to "会[あ]います", 
        "会って" to "会[あ]って", 
        "会った" to "会[あ]った", 
        "会える" to "会[あ]える", 
        "会う" to "会[あ]う", 
        "会え" to "会[あ]え", 
        "会おう" to "会[あ]おう", 
        "仕事" to "仕[し]事[ごと]", 
        "仕事をしています" to "仕[し]事[ごと]をしています", 
        "晴れた" to "晴[は]れた", 
        "土曜日" to "土[ど]曜[よう]日[び]", 
        "土曜" to "土[ど]曜[よう]", 
        "午後" to "午[ご]後[ご]", 
        "提案しました" to "提[てい]案[あん]しました", 
        "提案" to "提[てい]案[あん]", 
        "走って" to "走[はし]って", 
        "走る" to "走[はし]る", 
        "走りました" to "走[はし]りました", 
        "大笑いしました" to "大[おお]笑[わら]いしました", 
        "大笑い" to "大[おお]笑[わら]い", 
        "学校" to "学[がっ]校[こう]", 
        "乗って" to "乗[の]って", 
        "乗る" to "乗[の]る", 
        "行きます" to "行[い]きます", 
        "行く" to "行[い]く", 
        "行きました" to "行[い]きました", 
        "学生" to "学[がく]生[せい]", 
        "勉強" to "勉[べん]強[きょう]", 
        "日本語" to "日[に]本[ほん]語[ご]", 
        "私" to "私[わたし]", 
        "朝" to "朝[あさ]", 
        "七時" to "七[しち]時[じ]", 
        "目覚まし時計" to "目[め]覚[ざ]まし時[と]計[けい]", 
        "起きました" to "起[お]きました", 
        "制服" to "制[せい]服[ふく]", 
        "朝ごはん" to "朝[あさ]ごはん", 
        "食べました" to "食[た]べました", 
        "食べる" to "食[た]べる", 
        "元気" to "元[げん]気[き]", 
        "声" to "声[こえ]", 
        "言って" to "言[い]って", 
        "言う" to "言[い]う", 
        "玄関" to "玄[げん]関[かん]", 
        "出ました" to "出[で]ました", 
        "出る" to "出[で]る", 
        "爽やか" to "爽[さわ]やか", 
        "風" to "風[かぜ]", 
        "感じながら" to "感[かん]じながら", 
        "駅" to "駅[えき]", 
        "十分" to "十[じゅっ]分[ぷん]", 
        "歩きました" to "歩[ある]きました", 
        "歩いて" to "歩[ある]いて", 
        "歩く" to "歩[ある]く", 
        "満員電車" to "満[まん]員[いん]電[でん]車[しゃ]", 
        "揺られて" to "揺[ゆ]られて", 
        "高校" to "高[こう]校[こう]", 
        "着きました" to "着[つ]きました", 
        "着く" to "着[つ]く", 
        "校門" to "校[こう]門[もん]", 
        "前" to "前[まえ]", 
        "友達" to "友[とも]達[だち]", 
        "先生" to "先[せん]生[せい]", 
        "挨拶" to "挨[あい]拶[さつ]", 
        "新しい" to "新[あたら]しい", 
        "一日" to "一[いち]日[にち]", 
        "始まり" to "始[はじ]まり", 
        "夕方" to "夕[ゆう]方[がた]", 
        "近く" to "近[ちか]く", 
        "美味しい" to "美[おい]味[し]い", 
        "作ります" to "作[つく]ります", 
        "作る" to "作[つく]る", 
        "野菜" to "野[や]菜[さい]", 
        "売り場" to "売[う]り場[ば]", 
        "新鮮" to "新[しん]鮮[せん]", 
        "玉ねぎ" to "玉[たま]ねぎ", 
        "人参" to "人[にん]参[じん]", 
        "選びました" to "選[えら]びました", 
        "選ぶ" to "選[えら]ぶ", 
        "お肉" to "お肉[にく]", 
        "肉" to "肉[にく]", 
        "牛肉" to "牛[ぎゅう]肉[にく]", 
        "買いました" to "買[か]いました", 
        "買う" to "買[か]う", 
        "お金" to "お金[かね]", 
        "払って" to "払[はら]って", 
        "払う" to "払[はら]う", 
        "袋" to "袋[ふくろ]", 
        "詰めました" to "詰[つ]めました", 
        "詰める" to "詰[つ]める", 
        "早く" to "早[はや]く", 
        "家" to "家[いえ]", 
        "帰って" to "帰[かえ]って", 
        "帰る" to "帰[かえ]る", 
        "料理" to "料[りょう]理[り]", 
        "始める" to "始[はじ]める", 
        "楽しみ" to "楽[たの]しみ", 
        "公園" to "公[こう]園[えん]", 
        "桜" to "桜[さくら]", 
        "木" to "木[き]", 
        "陰" to "陰[かげ]", 
        "静かに" to "静[しず]かに", 
        "隠れました" to "隠[かく]れました", 
        "隠れる" to "隠[かく]れる", 
        "笑顔" to "笑[え]顔[がお]", 
        "見つけました" to "見[み]つけました", 
        "叫んで" to "叫[さけ]んで", 
        "叫ぶ" to "叫[さけ]ぶ", 
        "二人" to "二[ふた]人[り]", 
        "大笑い" to "大[おお]笑[わら]い", 
        "日" to "日[ひ]", 
        "暮れる" to "暮[く]れる", 
        "学びながら" to "学[まな]びながら", 
        "仲良く" to "仲[なか]良[よ]く", 
        "遊びました" to "遊[あそ]びました", 
        "遊ぶ" to "遊[あそ]ぶ", 
        "靴" to "靴[くつ]", 
        "脱ぎます" to "脱[ぬ]ぎます", 
        "脱ぐ" to "脱[ぬ]ぐ", 
        "温かい" to "温[あたた]かい", 
        "隣" to "隣[となり]", 
        "和室" to "和[わ]室[しつ]", 
        "畳" to "畳[たたみ]", 
        "良い" to "良[よ]い", 
        "香り" to "香[かお]り", 
        "広がって" to "広[ひろ]がって", 
        "広がっています" to "広[ひろ]がっています", 
        "障子" to "障[しょう]子[じ]", 
        "開けると" to "開[あ]けると", 
        "庭" to "庭[にわ]", 
        "見えます" to "見[み]えます", 
        "見る" to "見[み]る", 
        "台所" to "台[だい]所[どころ]", 
        "お母さん" to "お母[かあ]さん", 
        "お味噌汁" to "お味[み]噌[そ]汁[しる]", 
        "匂い" to "匂[にお]い", 
        "過ごす" to "過[す]ごす", 
        "大好き" to "大[だい]好[す]き", 
        "大学" to "大[だい]学[がく]", 
        "通っています" to "通[かよ]っています", 
        "通う" to "通[かよ]う", 
        "毎朝" to "毎[まい]朝[あさ]", 
        "起きます" to "起[お]きます", 
        "起きる" to "起[お]きる", 
        "願い" to "願[ねが]い" 
    ) 
    
    fun isKanji(c: Char): Boolean = c in '\u4E00'..'\u9FAF' || c in '\u3400'..'\u4DBF' || c == '々' 
    
    val singleKanjiReadings: Map<Char, String> = mapOf( 
        '日' to "ひ", '本' to "ほん", '人' to "ひと", '月' to "つき", '火' to "ひ", 
        '水' to "みず", '木' to "き", '金' to "かね", '土' to "つち", '子' to "こ", 
        '女' to "おんな", '男' to "おとこ", '学' to "がく", '生' to "せい", '先' to "せん", 
        '私' to "わたし", '年' to "とし", '何' to "なに", '時' to "じ", '間' to "かん", 
        '分' to "ふん", '今' to "いま", '前' to "まえ", '後' to "あと", '上' to "うえ", 
        '下' to "した", '中' to "なか", '外' to "そと", '大' to "おお", '小' to "ちい", 
        '高' to "たか", '安' to "やす", '新' to "あたら", '古' to "ふる", '長' to "なが", 
        '短' to "みじか", '白' to "しろ", '黒' to "くろ", '赤' to "あか", '青' to "あお", 
        '明' to "あか", '暗' to "くら", '円' to "えん", '行' to "い", '来' to "き", 
        '帰' to "かえ", '食' to "た", '飲' to "の", '見' to "み", '聞' to "き", 
        '読' to "よ", '書' to "か", '話' to "はな", '買' to "か", '売' to "う", 
        '作' to "つく", '待' to "ま", '持' to "も", '会' to "あ", '言' to "い", 
        '思' to "おも", '知' to "し", '立' to "た", '座' to "すわ", '休' to "やす", 
        '出' to "で", '入' to "はい", '走' to "はし", '歩' to "ある", '車' to "くるま", 
        '電' to "でん", '駅' to "えき", '道' to "みち", '校' to "こう", '店' to "みせ", 
        '家' to "いえ", '室' to "しつ", '屋' to "や", '友' to "とも", '達' to "だち", 
        '母' to "はは", '父' to "ちち", '兄' to "あに", '弟' to "おとうと", '姉' to "あね", 
        '妹' to "いもうと", '手' to "て", '目' to "め", '耳' to "みみ", '口' to "くち", 
        '足' to "あし", '体' to "からだ", '頭' to "あたま", '心' to "こころ", '気' to "き", 
        '天' to "てん", '雨' to "あめ", '雪' to "ゆき", '風' to "かぜ", '空' to "そら", 
        '海' to "うみ", '山' to "やま", '川' to "かわ", '花' to "はな", '桜' to "さくら", 
        '森' to "もり", '林' to "はやし", '魚' to "さかな", '肉' to "にく", '犬' to "いぬ", 
        '猫' to "ねこ", '鳥' to "とり", '町' to "まち", '国' to "くに", '語' to "ご", 
        '英' to "えい", '社' to "しゃ", '員' to "いん", '仕' to "し", '事' to "ごと", 
        '病' to "びょう", '院' to "いん", '薬' to "くすり", '朝' to "あさ", '昼' to "ひる", 
        '夜' to "よる", '夕' to "ゆう", '方' to "かた", '番' to "ばん", '号' to "ごう", 
        '物' to "もの", '理' to "り", '科' to "か", '勉' to "べん", '強' to "きょう", 
        '教' to "おし", '習' to "なら", '試' to "し", '験' to "けん", '問' to "もん", 
        '題' to "だい", '答' to "こた", '忘' to "わす", '始' to "はじ", '終' to "お", 
        '開' to "あ", '閉' to "し", '運' to "うん", '転' to "てん", '動' to "うご", 
        '止' to "と", '楽' to "たの", '歌' to "うた", '声' to "こえ", '音' to "おと", 
        '映' to "えい", '画' to "が", '写' to "しゃ", '真' to "しん", '旅' to "たび", 
        '宿' to "やど", '世' to "せ", '界' to "かい", '神' to "かみ", '寺' to "てら", 
        '門' to "もん", '階' to "かい", '段' to "だん", '窓' to "まど", '庭' to "にわ", 
        '街' to "まち", '村' to "むら", '島' to "しま", '光' to "ひかり", '影' to "かげ", 
        '星' to "ほし", '雲' to "くも", '石' to "いし", '竹' to "たけ", '草' to "くさ", 
        '茶' to "ちゃ", '飯' to "はん", '酒' to "さけ", '湯' to "ゆ", '紙' to "かみ", 
        '服' to "ふく", '着' to "き", '靴' to "くつ", '荷' to "に", '箱' to "はこ", 
        '春' to "はる", '夏' to "なつ", '秋' to "あき", '冬' to "ふゆ", '色' to "いろ", 
        '赤' to "あか", '黄' to "き", '緑' to "みどり", '紫' to "むらさき", '寒' to "さむ", 
        '暑' to "あつ", '熱' to "あつ", '温' to "あたた", '涼' to "すず", '忙' to "いそが", 
        '暇' to "ひま", '近' to "ちか", '遠' to "とお", '早' to "はや", '速' to "はや", 
        '遅' to "おそ", '重' to "おも", '軽' to "かる", '強' to "つよ", '弱' to "よわ", 
        '深' to "ふか", '浅' to "あさ", '広' to "ひろ", '狭' to "せま", '多' to "おお", 
        '少' to "すく", '静' to "しず", '賑' to "にぎ", '好' to "す", '嫌' to "きら", 
        '美' to "うつく", '味' to "あじ", '笑' to "わら", '泣' to "な", '怒' to "おこ", 
        '喜' to "よろこ", '悲' to "かな", '驚' to "おどろ", '信' to "しん", '愛' to "あい", 
        '恋' to "こい", '夢' to "ゆめ", '願' to "ねが", '祈' to "いの", '助' to "たす", 
        '守' to "まも", '戦' to "たたか", '勝' to "か", '負' to "ま", '剣' to "けん", 
        '刀' to "かたな", '弓' to "ゆみ", '矢' to "や", '盾' to "たて", '鎧' to "よろい", 
        '魔' to "ま", '法' to "ほう", '術' to "じゅつ", '力' to "ちから", 
        '妖' to "よう", '怪' to "かい", '鬼' to "おに", '竜' to "りゅう", '龍' to "りゅう", 
        '王' to "おう", '姫' to "ひめ", '城' to "しろ", '都' to "みやこ", '京' to "きょう", 
        '府' to "ふ", '県' to "けん", '市' to "し", '区' to "く", '館' to "かん", 
        '堂' to "どう", '塔' to "とう", '橋' to "はし", '池' to "いけ", '湖' to "みずうみ", 
        '波' to "なみ", '浜' to "はま", '岸' to "きし", '港' to "みなと", 
        '船' to "ふね", '航' to "こう", '飛' to "と", '落' to "お", 
        '降' to "お", '登' to "のぼ", '渡' to "わた", '過' to "す", '進' to "すす", 
        '追' to "お", '逃' to "に", '探' to "さが", '隠' to "かく", 
        '拾' to "ひろ", '捨' to "す", '選' to "えら", '置' to "お", '取' to "と", 
        '受' to "う", '送' to "おく", '届' to "とど", '集' to "あつ", 
        '散' to "さん", '晴' to "は", '曇' to "くも", '照' to "て", 
        '輝' to "かがや", '響' to "ひび", '震' to "ふる", '揺' to "ゆ", 
        '眠' to "ねむ", '覚' to "さ", '起' to "お", '疲' to "つか", '痛' to "いた", 
        '治' to "なお", '変' to "か", '直' to "なお", '整' to "ととの", '備' to "そな" 
    ) 
    
    fun getKanjiReading(text: String): String? { 
        if (text.isBlank()) return null 
        commonFuriganaMap[text]?.let { return it } 
        if (text.length == 1) { 
            singleKanjiReadings[text[0]]?.let { return it } 
        } 
        val sb = StringBuilder() 
        for (c in text) { 
            if (isKanji(c)) { 
                val r = singleKanjiReadings[c] ?: return null 
                sb.append(r) 
            } else { 
                sb.append(c) 
            } 
        } 
        return if (sb.isNotEmpty()) sb.toString() else null 
    } 
    
    fun resolveFallbackSegments(surface: String): List<RubySegment> { 
        if (surface.isBlank()) return emptyList() 
        val segments = mutableListOf<RubySegment>() 
        for (c in surface) { 
            if (isKanji(c)) { 
                val r = singleKanjiReadings[c] 
                segments.add(RubySegment(text = c.toString(), ruby = r)) 
            } else { 
                segments.add(RubySegment(text = c.toString(), ruby = null)) 
            } 
        } 
        return segments 
    } 
    
    private fun isKatakana(c: Char): Boolean = c in '\u30A0'..'\u30FF' 
    
    private fun isHiragana(c: Char): Boolean = c in '\u3040'..'\u309F' 
    
    private fun isPunctuation(c: Char): Boolean { 
        return c == '。' || c == '、' || c == '！' || c == '？' || 
               c == '「' || c == '」' || c == '（' || c == '）' || 
               c == '・' || c == '…' || c == '　' || c == ' ' 
    } 
    
    fun parseBracketSegments(text: String): List<RubySegment> { 
        val normalized = text.replace(Regex("<ruby>([^<]+)<rt>([^<]+)</rt></ruby>"), "$1[$2]") 
            .replace(Regex("<r>([^<]+)</r>"), "[$1]") 
        val segments = mutableListOf<RubySegment>() 
        var cursor = 0 
        while (cursor < normalized.length) { 
            val bracketOpen = normalized.indexOf('[', cursor) 
            if (bracketOpen == -1) { 
                val remaining = normalized.substring(cursor) 
                if (remaining.isNotEmpty()) segments.add(RubySegment(text = remaining, ruby = null)) 
                break 
            } 
            val bracketClose = normalized.indexOf(']', bracketOpen) 
            if (bracketClose == -1) { 
                val remaining = normalized.substring(cursor) 
                if (remaining.isNotEmpty()) segments.add(RubySegment(text = remaining, ruby = null)) 
                break 
            } 
            val ruby = normalized.substring(bracketOpen + 1, bracketClose).trim() 
            val beforeBracket = normalized.substring(cursor, bracketOpen) 
            if (beforeBracket.isNotEmpty()) { 
                val kanjiStart = beforeBracket.indexOfLast { !isKanji(it) && it != '々' } + 1 
                if (kanjiStart > 0) { 
                    val prefix = beforeBracket.substring(0, kanjiStart) 
                    segments.add(RubySegment(text = prefix, ruby = null)) 
                } 
                val kanjiWord = beforeBracket.substring(kanjiStart) 
                if (kanjiWord.isNotEmpty()) { 
                    segments.add(RubySegment(text = kanjiWord, ruby = ruby)) 
                } 
            } 
            cursor = bracketClose + 1 
        } 
        return if (segments.isEmpty()) listOf(RubySegment(text = text, ruby = null)) else segments 
    } 
    
    private fun buildSegments(surface: String, rawReading: String): List<RubySegment> { 
        if (surface.all { !isKanji(it) }) { 
            return listOf(RubySegment(text = surface, ruby = null)) 
        } 
        
        if (rawReading.isBlank()) { 
            return resolveFallbackSegments(surface) 
        } 
        
        if (rawReading.contains("[") && rawReading.contains("]")) { 
            return parseBracketSegments(rawReading) 
        } 
        if (rawReading.contains("<rt>") || rawReading.contains("<r>")) { 
            return parseBracketSegments(rawReading) 
        } 
        
        var firstHiraganaIdx = -1 
        for (i in surface.indices) { 
            if (isHiragana(surface[i])) { 
                firstHiraganaIdx = i 
                break 
            } 
        } 
        
        if (firstHiraganaIdx > 0) { 
            val kanjiPart = surface.substring(0, firstHiraganaIdx) 
            val okuriganaPart = surface.substring(firstHiraganaIdx) 
            val readingClean = if (rawReading.endsWith(okuriganaPart) && rawReading.length > okuriganaPart.length) { 
                rawReading.substring(0, rawReading.length - okuriganaPart.length) 
            } else { 
                rawReading 
            } 
            return listOf( 
                RubySegment(text = kanjiPart, ruby = readingClean), 
                RubySegment(text = okuriganaPart, ruby = null) 
            ) 
        } 
        
        return listOf(RubySegment(text = surface, ruby = rawReading)) 
    } 
    
    fun extractBracketWordToken(text: String, targetWords: List<StoryWordItem> = emptyList()): Pair<StoryToken, Int>? { 
        val kanjiBracketRegex = Regex("^(([\\u4E00-\\u9FAF\\u3400-\\u4DBF々]+)\\[([^\\]]+)\\])+") 
        val kanjiMatch = kanjiBracketRegex.find(text) ?: return null 
        val kanjiBracketPart = kanjiMatch.value 
        var okuriEnd = kanjiBracketPart.length 
        
        while (okuriEnd < text.length && isHiragana(text[okuriEnd])) { 
            val sub = text.substring(okuriEnd) 
            if (commonParticles.any { sub.startsWith(it) }) { 
                val currentCandidate = parseBracketSegments(text.substring(0, okuriEnd)).joinToString("") { it.text } 
                if (commonWordMeanings.containsKey(currentCandidate) || targetWords.any { it.kanji == currentCandidate }) { 
                    break 
                } 
                if (sub.startsWith("は") || sub.startsWith("が") || sub.startsWith("を") || 
                    sub.startsWith("で") || sub.startsWith("と") || sub.startsWith("も") || 
                    sub.startsWith("の") || sub.startsWith("へ") || sub.startsWith("から") || 
                    sub.startsWith("まで")) { 
                    break 
                } 
            } 
            okuriEnd++ 
        } 
        
        val matchedStr = text.substring(0, okuriEnd) 
        val segments = parseBracketSegments(matchedStr) 
        val surface = segments.joinToString("") { it.text } 
        val matchedTarget = targetWords.firstOrNull { it.kanji == surface } 
        val meaning = matchedTarget?.meaning?.ifBlank { commonWordMeanings[surface] ?: "" } ?: (commonWordMeanings[surface] ?: "") 
        val isTarget = matchedTarget != null 
        
        return Pair( 
            StoryToken( 
                surface = surface, 
                segments = segments, 
                isTarget = isTarget, 
                meaning = meaning 
            ), 
            matchedStr.length 
        ) 
    } 
    
    fun tokenizeToStoryTokens(sentence: String, targetWords: List<StoryWordItem>): List<StoryToken> { 
        if (sentence.isBlank()) return emptyList() 
        val result = mutableListOf<StoryToken>() 
        var remaining = sentence.trim() 
        
        while (remaining.isNotEmpty()) { 
            if (remaining.startsWith("[")) { 
                val closeIdx = remaining.indexOf(']') 
                if (closeIdx != -1) { 
                    remaining = remaining.substring(closeIdx + 1) 
                } else { 
                    remaining = remaining.substring(1) 
                } 
                continue 
            } 
            
            val bracketWord = extractBracketWordToken(remaining, targetWords) 
            if (bracketWord != null) { 
                result.add(bracketWord.first) 
                remaining = remaining.substring(bracketWord.second) 
                continue 
            } 
            
            val firstChar = remaining[0] 
            
            if (isPunctuation(firstChar)) { 
                if (firstChar != ' ' && firstChar != '　') { 
                    result.add( 
                        StoryToken( 
                            surface = firstChar.toString(), 
                            segments = listOf(RubySegment(text = firstChar.toString())), 
                            isPunctuation = true 
                        ) 
                    ) 
                } 
                remaining = remaining.substring(1) 
                continue 
            } 
            
            val matchedName = characterNames.firstOrNull { remaining.startsWith(it) } 
            if (matchedName != null) { 
                result.add( 
                    StoryToken( 
                        surface = matchedName, 
                        segments = listOf(RubySegment(text = matchedName)), 
                        isName = true, 
                        meaning = "Character" 
                    ) 
                ) 
                remaining = remaining.substring(matchedName.length) 
                continue 
            } 
            
            val matchedTarget = targetWords 
                .filter { it.kanji.isNotBlank() && remaining.startsWith(it.kanji) } 
                .maxByOrNull { it.kanji.length } 
                
            if (matchedTarget != null) { 
                val reading = matchedTarget.reading.ifBlank { commonFuriganaMap[matchedTarget.kanji] ?: "" } 
                val segments = buildSegments(matchedTarget.kanji, reading) 
                val meaning = matchedTarget.meaning.ifBlank { commonWordMeanings[matchedTarget.kanji] ?: "" } 
                result.add( 
                    StoryToken( 
                        surface = matchedTarget.kanji, 
                        segments = segments, 
                        isTarget = true, 
                        meaning = meaning 
                    ) 
                ) 
                remaining = remaining.substring(matchedTarget.kanji.length) 
                continue 
            } 
            
            val matchedAux = commonAuxiliaries.firstOrNull { remaining.startsWith(it) } 
            if (matchedAux != null) { 
                result.add( 
                    StoryToken( 
                        surface = matchedAux, 
                        segments = listOf(RubySegment(text = matchedAux)) 
                    ) 
                ) 
                remaining = remaining.substring(matchedAux.length) 
                continue 
            } 
            
            val matchedParticle = commonParticles.firstOrNull { remaining.startsWith(it) } 
            if (matchedParticle != null) { 
                result.add( 
                    StoryToken( 
                        surface = matchedParticle, 
                        segments = listOf(RubySegment(text = matchedParticle)) 
                    ) 
                ) 
                remaining = remaining.substring(matchedParticle.length) 
                continue 
            } 
            
            if (isKatakana(firstChar)) { 
                var katakanaEnd = 1 
                while (katakanaEnd < remaining.length && (isKatakana(remaining[katakanaEnd]) || remaining[katakanaEnd] == 'ー')) { 
                    katakanaEnd++ 
                } 
                val kataWord = remaining.substring(0, katakanaEnd) 
                result.add( 
                    StoryToken( 
                        surface = kataWord, 
                        segments = listOf(RubySegment(text = kataWord)) 
                    ) 
                ) 
                remaining = remaining.substring(katakanaEnd) 
                continue 
            } 
            
            if (isKanji(firstChar)) { 
                val knownKanjiWord = commonFuriganaMap.keys 
                    .filter { remaining.startsWith(it) } 
                    .maxByOrNull { it.length } 
                    
                if (knownKanjiWord != null) { 
                    val furi = commonFuriganaMap[knownKanjiWord] ?: "" 
                    val segments = buildSegments(knownKanjiWord, furi) 
                    result.add( 
                        StoryToken( 
                            surface = knownKanjiWord, 
                            segments = segments, 
                            meaning = commonWordMeanings[knownKanjiWord] ?: "" 
                        ) 
                    ) 
                    remaining = remaining.substring(knownKanjiWord.length) 
                    continue 
                } 
                
                var kanjiEnd = 1 
                while (kanjiEnd < remaining.length && isKanji(remaining[kanjiEnd])) { 
                    kanjiEnd++ 
                } 
                while (kanjiEnd < remaining.length && isHiragana(remaining[kanjiEnd])) { 
                    val sub = remaining.substring(kanjiEnd) 
                    if (commonParticles.any { sub.startsWith(it) } || commonAuxiliaries.any { sub.startsWith(it) }) { 
                        break 
                    } 
                    kanjiEnd++ 
                } 
                val compound = remaining.substring(0, kanjiEnd) 
                val reading = commonFuriganaMap[compound] ?: "" 
                val segments = buildSegments(compound, reading) 
                result.add( 
                    StoryToken( 
                        surface = compound, 
                        segments = segments, 
                        meaning = commonWordMeanings[compound] ?: "" 
                    ) 
                ) 
                remaining = remaining.substring(kanjiEnd) 
                continue 
            } 
            
            val matchedHiragana = commonHiraganaPhrases.firstOrNull { remaining.startsWith(it) } 
            if (matchedHiragana != null) { 
                result.add( 
                    StoryToken( 
                        surface = matchedHiragana, 
                        segments = listOf(RubySegment(text = matchedHiragana)), 
                        meaning = commonWordMeanings[matchedHiragana] ?: "" 
                    ) 
                ) 
                remaining = remaining.substring(matchedHiragana.length) 
                continue 
            } 
            
            var genericEnd = 1 
            while (genericEnd < remaining.length) { 
                val sub = remaining.substring(genericEnd) 
                val nextChar = remaining[genericEnd] 
                if (isPunctuation(nextChar) || isKanji(nextChar) || isKatakana(nextChar) || 
                    characterNames.any { sub.startsWith(it) } || 
                    targetWords.any { it.kanji.isNotBlank() && sub.startsWith(it.kanji) } || 
                    commonAuxiliaries.any { sub.startsWith(it) } || 
                    commonParticles.any { sub.startsWith(it) }) { 
                    break 
                } 
                genericEnd++ 
            } 
            val chunk = remaining.substring(0, genericEnd) 
            result.add( 
                StoryToken( 
                    surface = chunk, 
                    segments = listOf(RubySegment(text = chunk)) 
                ) 
            ) 
            remaining = remaining.substring(genericEnd) 
        } 
        
        return result 
    } 
    
    fun tokenize(sentence: String, targetWords: List<StoryWordItem>): List<StoryWordItem> { 
        return tokenizeToStoryTokens(sentence, targetWords).map { 
            StoryWordItem( 
                kanji = it.surface, 
                reading = it.segments.firstOrNull { s -> s.ruby != null }?.ruby ?: "", 
                meaning = it.meaning, 
                pos = if (it.isName) "name" else if (it.isPunctuation) "punct" else "" 
            ) 
        } 
    } 
}
