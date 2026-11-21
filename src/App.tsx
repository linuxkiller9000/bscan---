import {
  CheckCircle2,
  Clock,
  Cpu,
  FolderTree,
  HardDrive,
  Lock,
  ScanLine,
  Smartphone,
  Terminal,
} from 'lucide-react';

const IMPLEMENTED_ITEMS = [
  'Gradle Kotlin DSL project with version catalog (AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.10.01, Room 2.6.1, KSP)',
  'BscanTheme Material 3 design system (#0B1220 navy background, #121D2D surfaces, #38BDF8 cyan accent, restrained geometry)',
  'Bottom navigation across Home, Projects, and Settings plus Camera foundation and Document Preview routes',
  'Real Android Photo Picker (PickMultipleVisualMedia) with OpenMultipleDocuments fallback for API 26-35',
  'Immutable original storage in app-private filesDir with atomic temp-file writes and EXIF-aware downscaled thumbnails',
  'Offline Room database persisting projects and ordered pages with rename and safe delete',
  'Runtime CAMERA permission rationale, resume re-check, and permanent-denial settings redirect',
  'Settings screen with honest Tesseract 5 language catalog (7 languages marked Not installed), Snapdragon 712 memory settings, and live storage usage / cache clearing',
  'English default strings + Arabic localization resources (values-ar/strings.xml) and android:supportsRtl="true"',
];

const UPCOMING_ITEMS = [
  'CameraX live preview, flash control, and automatic page boundary detection (Milestone 2)',
  'OpenCV perspective warp, manual corner adjustment, rotation, deskew, and image enhancement',
  'Basic geometric book-page dewarping using traditional computer vision',
  'Tesseract 5 local OCR engine, traineddata pack management, and RTL Arabic review editor',
  'Multi-page PDF, searchable PDF, RTL-safe DOCX, TXT, and JPEG/PNG export',
  'WorkManager + Foreground Service job orchestration with thermal pause/resume',
];

const PACKAGES = [
  { name: 'com.bscan.app', desc: 'BscanApplication, MainActivity, AppContainer, BscanNavGraph' },
  { name: 'core.model', desc: 'ScanProject, ScanPage, OcrLanguage, ProcessingPreferences' },
  { name: 'core.data', desc: 'Room BscanDatabase, ProjectDao, AppFileManager, SampledBitmapLoader, PreferencesStore' },
  { name: 'core.ui', desc: 'BscanTheme, Color/Type/Shape tokens, BscanWordmark, SampledLocalImage, ProjectListItemCard' },
  { name: 'feature.home', desc: 'HomeScreen, HomeViewModel (Scan document + Import photos + Recent projects)' },
  { name: 'feature.projects', desc: 'ProjectsScreen, ProjectsViewModel (library list, rename, delete)' },
  { name: 'feature.importmedia', desc: 'GalleryImagePicker, ImportedPreviewScreen, ImportedPreviewViewModel' },
  { name: 'feature.camera', desc: 'CameraFoundationScreen (runtime CAMERA permission & Milestone 2 boundary)' },
  { name: 'feature.settings', desc: 'SettingsScreen, SettingsViewModel (OCR catalog, memory caps, storage)' },
  { name: 'processing', desc: 'ImageInspector (bounds, inSampleSize, EXIF transform foundation)' },
];

export default function App() {
  return (
    <div className="min-h-screen bg-[#0B1220] text-[#F1F5F9]">
      <div className="mx-auto max-w-5xl px-6 py-10">
        {/* Header */}
        <header className="flex flex-wrap items-center justify-between gap-4 border-b border-[#223149] pb-6">
          <div className="flex items-center gap-3.5">
            <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-[#102A3E] text-[#38BDF8]">
              <ScanLine className="h-6 w-6" />
            </div>
            <div>
              <div className="flex items-center gap-2.5">
                <h1 className="text-2xl font-semibold tracking-tight text-[#F1F5F9]">bscan</h1>
                <span className="h-4 w-px bg-[#314666]" />
                <span dir="rtl" className="text-lg font-medium text-[#94A3B8]">
                  بسكان
                </span>
              </div>
              <p className="text-sm text-[#64748B]">
                Independent Offline Android Document Scanner · Milestone 1 Foundation
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2 rounded-md border border-[#223149] bg-[#121D2D] px-3 py-1.5 text-xs font-medium text-[#38BDF8]">
            <Smartphone className="h-3.5 w-3.5" />
            <span>com.bscan.app · minSdk 26 · targetSdk 35</span>
          </div>
        </header>

        {/* Notice */}
        <section className="mt-6 rounded-xl border border-[#223149] bg-[#121D2D] p-5">
          <div className="flex items-start gap-3">
            <Lock className="mt-0.5 h-5 w-5 shrink-0 text-[#34D399]" />
            <div className="space-y-1">
              <h2 className="text-sm font-semibold text-[#F1F5F9]">
                Native Android Project Workspace (/android)
              </h2>
              <p className="text-sm leading-relaxed text-[#94A3B8]">
                This browser view summarizes the real native Kotlin + Jetpack Compose Android
                application located in <code className="rounded bg-[#0B1220] px-1.5 py-0.5 text-xs text-[#38BDF8]">/android</code>.
                No web mockup or simulated scanner is substituted for the Android app. The Android
                manifest omits <code className="rounded bg-[#0B1220] px-1.5 py-0.5 text-xs text-[#F1F5F9]">INTERNET</code> permission
                to enforce offline operation at the OS level.
              </p>
            </div>
          </div>
        </section>

        {/* Specs Grid */}
        <section className="mt-6 grid gap-4 sm:grid-cols-3">
          <div className="rounded-xl border border-[#223149] bg-[#121D2D] p-4">
            <div className="flex items-center gap-2 text-xs font-medium text-[#94A3B8]">
              <Cpu className="h-4 w-4 text-[#38BDF8]" />
              <span>Target Hardware</span>
            </div>
            <p className="mt-2 text-sm font-medium text-[#F1F5F9]">Realme 5 Pro (Snapdragon 712)</p>
            <p className="mt-1 text-xs text-[#64748B]">
              Single-page sequential processing, sampled previews, 16 MB bitmap LRU cap.
            </p>
          </div>
          <div className="rounded-xl border border-[#223149] bg-[#121D2D] p-4">
            <div className="flex items-center gap-2 text-xs font-medium text-[#94A3B8]">
              <HardDrive className="h-4 w-4 text-[#38BDF8]" />
              <span>Storage Model</span>
            </div>
            <p className="mt-2 text-sm font-medium text-[#F1F5F9]">Room + Read-Only Originals</p>
            <p className="mt-1 text-xs text-[#64748B]">
              Atomic temp-file writes into private filesDir/projects/&lt;id&gt;/originals.
            </p>
          </div>
          <div className="rounded-xl border border-[#223149] bg-[#121D2D] p-4">
            <div className="flex items-center gap-2 text-xs font-medium text-[#94A3B8]">
              <Terminal className="h-4 w-4 text-[#38BDF8]" />
              <span>Debug APK Build</span>
            </div>
            <code className="mt-2 block rounded bg-[#0B1220] px-2.5 py-1.5 text-xs text-[#38BDF8]">
              cd android &amp;&amp; ./gradlew assembleDebug
            </code>
            <p className="mt-1 text-xs text-[#64748B]">
              Output: app/build/outputs/apk/debug/app-debug.apk
            </p>
          </div>
        </section>

        {/* Scope Columns */}
        <section className="mt-6 grid gap-6 md:grid-cols-2">
          <div className="rounded-xl border border-[#223149] bg-[#121D2D] p-5">
            <div className="flex items-center gap-2 text-sm font-semibold text-[#F1F5F9]">
              <CheckCircle2 className="h-4 w-4 text-[#34D399]" />
              <span>Implemented in Milestone 1</span>
            </div>
            <ul className="mt-4 space-y-2.5 text-sm text-[#94A3B8]">
              {IMPLEMENTED_ITEMS.map((item) => (
                <li key={item} className="flex items-start gap-2.5">
                  <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-sm bg-[#34D399]" />
                  <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>

          <div className="rounded-xl border border-[#223149] bg-[#121D2D] p-5">
            <div className="flex items-center gap-2 text-sm font-semibold text-[#F1F5F9]">
              <Clock className="h-4 w-4 text-[#FBBF24]" />
              <span>Scheduled for Later Milestones</span>
            </div>
            <ul className="mt-4 space-y-2.5 text-sm text-[#94A3B8]">
              {UPCOMING_ITEMS.map((item) => (
                <li key={item} className="flex items-start gap-2.5">
                  <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-sm bg-[#64748B]" />
                  <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>
        </section>

        {/* Package Structure */}
        <section className="mt-6 rounded-xl border border-[#223149] bg-[#121D2D] p-5">
          <div className="flex items-center gap-2 text-sm font-semibold text-[#F1F5F9]">
            <FolderTree className="h-4 w-4 text-[#38BDF8]" />
            <span>Android Package Architecture (Single :app Module, Extractable Boundaries)</span>
          </div>
          <div className="mt-4 grid gap-3 sm:grid-cols-2">
            {PACKAGES.map((pkg) => (
              <div
                key={pkg.name}
                className="rounded-lg border border-[#223149] bg-[#0E1726] px-3.5 py-2.5"
              >
                <div className="font-mono text-xs font-medium text-[#38BDF8]">{pkg.name}</div>
                <div className="mt-1 text-xs text-[#94A3B8]">{pkg.desc}</div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
