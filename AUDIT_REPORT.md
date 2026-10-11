# Play Store release audit — O'zbek tilining izohli lug'ati (v1.2.1, versionCode 5)

## Muhim cheklov (avval o'qing)
Audit muhitida **Android SDK va internet yo'q** edi. Shuning uchun:
- `./gradlew assembleDebug/assembleRelease/test/connectedAndroidTest` **ishga tushirilmadi**.
- Ilova emulyator/qurilmada **ishga tushirilmadi**, Logcat, ANR, memory, rotation, process death, TalkBack testlari **o'tkazilmadi**.
- Quyidagi topilmalar — **statik tahlil** (barcha 17 Kotlin/Java fayl, layout, manifest, tema, Gradle, R8 qoidalari) va **bundle qilingan data.db ni haqiqiy tekshirish** natijasi.
- Tuzatishlar **kompilyatsiya qilinmagan**. Ular kichik, lekin Android Studio'da build qilib tekshirish shart.

Shu sababli "Play Store'ga tayyor" deb ayta olmayman. Pastda nima tekshirilgani va nima qolgani aniq ko'rsatilgan.

## PROJECT STATUS
| | |
|---|---|
| Build / Debug / Release | TEKSHIRILMAGAN (SDK yo'q) |
| Tests | Faqat shablon (ExampleUnitTest, ExampleInstrumentedTest); real test yo'q |
| Crashes / ANR / Memory / Performance | Runtime o'lchanmagan; statik xavflar pastda |
| Security | Statik: toza (sir/kalit yo'q, INTERNET yo'q, faqat MainActivity exported, FileProvider exported=false, Log yo'q) |
| targetSdk | 36 — Play Help bo'yicha 2026-08-31 dan yangi ilova/yangilanishlar uchun API 36 talab qilinadi → mos |
| Play readiness | Qurilmada test + release build tasdiqlanmaguncha: NOMA'LUM |

## Data bazasi (haqiqiy tekshiruv)
- `data.db`: integrity_check = ok; `dictionary` 33 025 qator, `atama` 83 635 qator.
- Room entity'lar bilan sxema qo'lda solishtirildi (ustunlar, nullability, indekslar `index_dictionary_word`, `index_atama_word`) — mos.
- 1 ta bo'sh description: `_id 4067 BURNAKI`. 63 ta description > 5000 belgi, eng uzuni 31 656.
- Atama'da 1 ta `�` (U+FFFD) va 1 ta `_` bor so'z — ma'lumot sifati masalasi.
- Barcha apostroflar ASCII `'` (4830 ta so'z). Bu BUG #1 ning sababi.
- Asset `user_version = 0`, Room `version = 2`: qarang R3.

## FIXED BUGS (kodda tuzatildi, build qilinmagan)

**BUG #1 — Apostrofli so'zlar qidiruvda topilmaydi** · HIGH
Asosiy funksiya. O'zbek klaviaturalari `ʻ`, `’`, `‘` yozadi, bazada esa faqat `'`.
Repro: qidiruvga `oʻ` yoki `a’lo` yozing → "topilmadi". Fix: so'rov `'` ga normallashtiriladi + trim (HomeFragment, AtamaFragment).

**BUG #2 — Android 15+/targetSdk 36 da status bar belgilari ko'rinmay qolishi** · MEDIUM (qurilmada tasdiqlanmagan)
`statusBarColor` e'tiborga olinmaydi; tepa chiziq oyna foni (#F6F8FC) bo'lib qoladi, belgilar esa oq. Fix: `activity_main.xml` ildizi `header_blue`.

**BUG #3 — TextToSpeech Android 11+ da ishlamasligi mumkin** · MEDIUM
Package visibility uchun `<queries>` yo'q edi. Fix: manifestga `TTS_SERVICE` intent qo'shildi.

**BUG #4 — Ro'yxatning oxirgi elementlari suzuvchi bottom bar ostida qoladi** · MEDIUM
RecyclerView `paddingBottom=20dp`, bar ≈ 66dp + 16dp + inset. Fix: Home va Atama'da 120dp.

**BUG #5 — Race: to'liq ro'yxat qidiruv natijasini bosib ketishi** · MEDIUM
`loadWords()` bekor qilinmaydi; aylantirishdan keyin tiklangan so'rov bilan poyga. Fix: `loadWords` ham `searchJob` ga yoziladi.

**BUG #6 — Atama: yuklanganda filtr qo'llanmaydi** · LOW
Aylantirishdan keyin qidiruv matni turadi, ro'yxat esa filtrlanmagan. Fix: yuklangach joriy so'rov bilan filtrlanadi.

**BUG #7 — Qidiruvda belgilash rangi (#0D47A1) tungi rejimda deyarli ko'rinmaydi** · LOW
Fix: `R.color.primary_blue` (kun/tun uchun alohida qiymat bor).

**BUG #8 — "Kunning so'zi" bildirishnomasi** · LOW
Bosilganda ochiq ilova ustiga ikkinchi MainActivity + splash ochilishi mumkin; matn cheklanmagan (description 31 KB gacha). Fix: `NEW_TASK | SINGLE_TOP`, matn 400 belgi.

**BUG #9 — Share: kesh PNG'lar to'planadi, stream xatoda yopilmaydi** · LOW
Fix: eski rasmlar o'chiriladi, `use {}`.

**BUG #10 — TTS `onInit` view yo'q bo'lgandan keyin chaqirilsa `requireContext()` IllegalStateException** · LOW
Fix: `isAdded` tekshiruvi, `tts = null`.

**BUG #11 — Settings'da "Versiya 1.1.0", haqiqiy 1.2.1** · LOW
Fix: satr yangilandi. (Keyingi relizda PackageInfo'dan olish yaxshiroq.)

**Gigiena:** keraksiz `allowMainThreadQueries()` olib tashlandi (barcha DAO `suspend`).

Shuni tekshirib ko'rdim va BUG emas: share karta `maxLines=5` — uzun description rasmni portlatmaydi; RealtimeBlurView RenderScript ishlamasa Java fallback'ga o'tadi; R8 qoidalarida `-keep class ** {*;}` yo'q.

## REMAINING ISSUES (tuzatilmagan)

**R1 — Build tasdiqlanmagan** · NOMA'LUM. AGP 9.3.2, Gradle 9.5.0, KSP 2.3.4, compileSdk 37 mosligini faqat Android Studio tasdiqlaydi. `assembleRelease` imzo parollarisiz (`RELEASE_STORE_PASSWORD` va h.k. ~/.gradle/gradle.properties da) null bilan yiqiladi.

**R2 — Xotira/performance xavfi** · MEDIUM–HIGH (o'lchanmagan). `getAllWords()` 33 025 ta yozuvni **to'liq description bilan** xotiraga yuklaydi (har bo'sh so'rovda ham). Kam RAM'li qurilmada OOM/uzoq yuklanish mumkin. Tavsiya (arxitekturani buzmaydi, lekin build/test kerak): ro'yxat uchun faqat `_id, word` proyeksiyasi, description'ni bosilganda `_id` bo'yicha olish.

**R3 — Migratsiya** · tekshirish kerak. `fallbackToDestructiveMigration(true)` + `createFromAsset`. Avval Play'da chiqqan versiya o'rnatilgan qurilmada **yangilash testi** qiling (bazaning yangi nusxasi ko'chirilishini tasdiqlang).

**R4 — Detail'dan orqaga qaytganda Home ro'yxati tiklanadi, scroll yo'qoladi** · MEDIUM. ViewModel kerak (arxitektura o'zgarishi, shuning uchun tegmadim).

**R5 — Klaviatura (IME insets)** · LOW–MEDIUM. Edge-to-edge'da `adjustResize` ishlamaydi; klaviatura ro'yxat pastini yopishi mumkin. Qurilmada tekshiring.

**R6 — LIKE belgilari** · LOW. `%` va `_` qidiruvda wildcard sifatida ishlaydi (`ESCAPE` yo'q).

**R7 — Accessibility** · MEDIUM. 12 layout'da jami 4 ta `contentDescription`; orqaga, ovoz, bottom nav, qidiruv ikonlarida yo'q. Matnlar ko'p joyda qattiq yozilgan (layout/kodda), `strings.xml` ga ko'chirilmagan.

**R8 — Ikki marta splash** · LOW. `Theme.AppSplashScreen` hech qayerda qo'llanmagan; Android 12+ tizim splash + 2 soniyalik SplashFragment.

**R9 — Backup** · LOW–MEDIUM. `allowBackup=true`, qoidalar bo'sh: ~20 MB baza zaxiraga ketadi. Baza asset'dan qayta tiklanadi, shuning uchun `allowBackup=false` yoki bazani chiqarib tashlash ma'qul.

**R10 — Settings'dagi Telegram havolasi** · LOW. `startActivity` ACTION_VIEW `ActivityNotFoundException` ushlamaydi.

**R11 — Tozalash** · LOW. O'lik kod: `InfoFragment`, `getAtamaWordsByLetter`, `getInstance()`, `Theme.AppSplashScreen`. README eskirgan (Hilt/Compose/minSdk 21 deb yozilgan, aslida yo'q/24). `.kotlin`, `.artifacts` papkalari arxivda (tuzatilgan arxivdan olib tashlandi).

**Mazmunan qo'llanilmaydigan bo'limlar:** Firebase, Retrofit, Hilt, tarmoq, runtime ruxsatlar (faqat POST_NOTIFICATIONS) — loyihada yo'q.

## Keyingi qadamlar (o'zingizda, ~1 soat)
1. Android Studio'da: `./gradlew clean assembleDebug lint test` — xatolarni menga yuboring.
2. `./gradlew bundleRelease` (imzo parollari bilan) → `app-release.aab`; mapping.txt'ni saqlang.
3. Release APK'ni emulyatorlarda sinang: API 24, 34, 36 (light/dark, aylantirish, Home→Detail→Back, qidiruv `oʻ`/`a’lo`, share rasm/matn, ovoz, bildirishnoma).
4. Eski versiya ustiga yangilash testi (R3).
5. Menga Logcat/stack trace yuborsangiz, R2 va R4 ni minimal tarzda tuzataman.
