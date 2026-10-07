# Documentation Index

Panduan lengkap untuk menggunakan repository Virtual Gamepad.

## 🚀 Mulai Cepat

**Baru di project? Mulai dari sini:**

1. **[QUICK_START.md](QUICK_START.md)** - Setup dalam 5 menit
   - Prerequisites
   - Build
   - Deploy
   - Test

2. **Pilih platform Anda:**
   - **[COMPILATION_INSTRUCTIONS.md](COMPILATION_INSTRUCTIONS.md)** - Windows detailed guide
   - **[BUILD_UBUNTU.md](BUILD_UBUNTU.md)** - Ubuntu quick reference
   - **[UBUNTU_BUILD_GUIDE.md](UBUNTU_BUILD_GUIDE.md)** - Ubuntu comprehensive guide

## 📖 Main Documentation

### Project Overview
- **[README.md](README.md)** - Project overview, architecture, features
  - Apa itu Virtual Gamepad
  - Bagaimana cara kerjanya
  - Technology stack
  - Performance metrics

### Platform-Specific
- **[android/README.md](android/README.md)** - Android app details
  - Features
  - Usage guide
  - Configuration
  - Troubleshooting

- **[pc-server/README.md](pc-server/README.md)** - PC server details
  - Installation
  - ViGEm setup
  - Firewall config
  - Troubleshooting

### Build Guides
- **[COMPILATION_INSTRUCTIONS.md](COMPILATION_INSTRUCTIONS.md)** - Complete build guide
  - Prerequisites installation
  - Android build (GUI + CLI)
  - PC server build
  - Troubleshooting
  - CI/CD integration

- **[UBUNTU_BUILD_GUIDE.md](UBUNTU_BUILD_GUIDE.md)** - Ubuntu specific
  - Detailed setup
  - Multiple options
  - Docker support
  - Ubuntu version differences

- **[BUILD_UBUNTU.md](BUILD_UBUNTU.md)** - Ubuntu quick ref
  - 3 build options
  - Quick troubleshooting
  - Tips and tricks

- **[UBUNTU_QUICK_BUILD.sh](UBUNTU_QUICK_BUILD.sh)** - Automated script
  - One-command build
  - Auto prerequisites
  - Error handling

## 📊 Reference Documentation

### Technical
- **[WINDOWS_VS_UBUNTU.md](WINDOWS_VS_UBUNTU.md)** - Platform differences
  - Build differences
  - File transfer
  - Performance comparison
  - Deployment options

- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Project statistics
  - File count and LOC
  - Architecture breakdown
  - Technology stack
  - Performance metrics

### Architecture
- Check `README.md` for:
  - Network protocol
  - Architecture diagram
  - Component breakdown

## 🎯 By Use Case

### "I want to use the app"
1. Read `QUICK_START.md`
2. Follow build steps
3. Install and run
4. Check `QUICK_START.md` troubleshooting

### "I want to build on Windows"
1. Read `COMPILATION_INSTRUCTIONS.md`
2. Install prerequisites section
3. Follow Windows build steps
4. Check troubleshooting section

### "I want to build on Ubuntu"
1. Read `BUILD_UBUNTU.md` for overview
2. Run `bash UBUNTU_QUICK_BUILD.sh` for automated
3. Or follow `UBUNTU_BUILD_GUIDE.md` for manual
4. Transfer EXE to Windows to run

### "I want to understand the architecture"
1. Read main `README.md`
2. Check Architecture section
3. Read code comments in android/java and pc-server/

### "I have a problem"
1. Check relevant README troubleshooting
2. Search `COMPILATION_INSTRUCTIONS.md`
3. Check `UBUNTU_BUILD_GUIDE.md` if Ubuntu
4. Check `WINDOWS_VS_UBUNTU.md` if platform issues

### "I want to contribute/modify code"
1. Understand project in `README.md`
2. Check architecture in `PROJECT_SUMMARY.md`
3. Read code comments in source
4. Follow existing code patterns
5. Update relevant documentation

## 📁 Directory Organization

```
gamepad/
├── README.md                        ← Start here for overview
├── QUICK_START.md                   ← 5-minute setup
├── COMPILATION_INSTRUCTIONS.md      ← Detailed build guide
├── BUILD_UBUNTU.md                  ← Ubuntu quick ref
├── UBUNTU_BUILD_GUIDE.md            ← Ubuntu detailed
├── UBUNTU_QUICK_BUILD.sh            ← Auto-build script
├── UBUNTU_SETUP.md                  ← Step-by-step Ubuntu
├── WINDOWS_VS_UBUNTU.md             ← Platform comparison
├── PROJECT_SUMMARY.md               ← Stats & overview
├── DOCS_INDEX.md                    ← This file
│
├── android/
│   ├── README.md                    ← Android documentation
│   ├── app/
│   │   └── src/main/java/...       ← Source code
│   └── build.gradle.kts
│
└── pc-server/
    ├── README.md                    ← PC server documentation
    └── VirtualGamepadServer.csproj
```

## 🔍 Finding What You Need

### Build Problems
- `COMPILATION_INSTRUCTIONS.md` → Troubleshooting section
- `UBUNTU_BUILD_GUIDE.md` → Troubleshooting section
- `BUILD_UBUNTU.md` → Quick ref table

### Understanding How It Works
- `README.md` → Architecture section
- `PROJECT_SUMMARY.md` → Architecture highlights
- `WINDOWS_VS_UBUNTU.md` → Understanding platforms

### Installation Issues
- Android: `android/README.md` → Troubleshooting
- PC: `pc-server/README.md` → Troubleshooting + Firewall setup
- Ubuntu: `UBUNTU_BUILD_GUIDE.md` → Prerequisites section

### Connection Issues
- `QUICK_START.md` → Troubleshooting section
- `android/README.md` → Troubleshooting
- `pc-server/README.md` → Troubleshooting

### Performance Issues
- `PROJECT_SUMMARY.md` → Performance metrics
- `pc-server/README.md` → Network optimization
- `UBUNTU_BUILD_GUIDE.md` → Build speed tips

## 📋 Documentation Checklist

For project maintainers:

- [x] Main README with overview
- [x] Quick start guide (5 min)
- [x] Detailed build guides (Windows)
- [x] Ubuntu-specific guides
- [x] Automated build script
- [x] Troubleshooting sections
- [x] Architecture documentation
- [x] Platform comparison
- [x] Project summary & stats
- [x] Documentation index (this file)
- [x] Android README
- [x] PC Server README
- [x] Protocol specification (in README)

## 🔄 Documentation Update Guide

When updating code, update corresponding docs:

| Code Change | Document Update |
|-------------|-----------------|
| Add feature | Update `README.md` features list |
| Change protocol | Update `README.md` protocol section |
| Add dependency | Update `COMPILATION_INSTRUCTIONS.md` |
| Fix build issue | Update troubleshooting sections |
| Change architecture | Update `PROJECT_SUMMARY.md` |

## 📞 Support Levels

### Level 1: Quick References
- `QUICK_START.md` (5 minutes)
- `BUILD_UBUNTU.md` (quick overview)
- `UBUNTU_SETUP.md` (step-by-step)

### Level 2: Comprehensive Guides
- `COMPILATION_INSTRUCTIONS.md` (Windows detail)
- `UBUNTU_BUILD_GUIDE.md` (Ubuntu detail)
- `android/README.md` (Android detail)
- `pc-server/README.md` (PC detail)

### Level 3: Deep Dives
- `PROJECT_SUMMARY.md` (architecture & stats)
- `README.md` (full architecture)
- `WINDOWS_VS_UBUNTU.md` (platform deep dive)
- Source code with comments

### Level 4: Debugging
- Check `.log` files
- Run scripts with `bash -x` for debug
- Check console output from apps
- Use `adb logcat` for Android

## ⚠️ Common Mistakes

Don't do this:
- ❌ Skip prerequisites installation
- ❌ Build PC on Ubuntu and try to run on Ubuntu (won't work - ViGEm Windows-only)
- ❌ Forget `chmod +x gradlew` on Ubuntu
- ❌ Run PC server without ViGEm installed
- ❌ Connect without being on same Wi-Fi network

Do this:
- ✅ Install ALL prerequisites first
- ✅ Build PC EXE on Windows or for Windows
- ✅ Make gradlew executable on Ubuntu
- ✅ Install ViGEm BEFORE running PC server
- ✅ Verify both on same network

## 🎓 Learning Resources

### For Beginners
1. Read `QUICK_START.md` - understand what to do
2. Run `UBUNTU_QUICK_BUILD.sh` or Windows build - see it work
3. Test with `QUICK_START.md` connection steps
4. Success! You've built and deployed a full system

### For Developers
1. Read `README.md` - understand architecture
2. Read code in `android/app/src/main/java/`
3. Read code in `pc-server/`
4. Understand how data flows end-to-end
5. Try modifying and rebuilding

### For Operators
1. Read `COMPILATION_INSTRUCTIONS.md`
2. Setup CI/CD pipeline (see CI/CD section)
3. Automate builds
4. Deploy artifacts
5. Monitor in production

## 📊 Statistics

| Metric | Value |
|--------|-------|
| **Documentation files** | 10+ |
| **Doc lines** | ~3,000+ |
| **Languages covered** | Windows, Ubuntu |
| **Build options** | 3+ |
| **Troubleshooting items** | 50+ |

## 🔗 Quick Links

| What | Where |
|------|-------|
| Start here | [README.md](README.md) |
| 5-min setup | [QUICK_START.md](QUICK_START.md) |
| Build on Windows | [COMPILATION_INSTRUCTIONS.md](COMPILATION_INSTRUCTIONS.md) |
| Build on Ubuntu | [BUILD_UBUNTU.md](BUILD_UBUNTU.md) or [UBUNTU_QUICK_BUILD.sh](UBUNTU_QUICK_BUILD.sh) |
| Android info | [android/README.md](android/README.md) |
| PC info | [pc-server/README.md](pc-server/README.md) |
| Platform diff | [WINDOWS_VS_UBUNTU.md](WINDOWS_VS_UBUNTU.md) |

## 💡 Pro Tips

1. **Bookmark QUICK_START.md** - refer to it often
2. **Keep COMPILATION_INSTRUCTIONS.md handy** - for builds
3. **Use UBUNTU_QUICK_BUILD.sh** - saves time
4. **Check troubleshooting first** - most issues covered
5. **Read code comments** - learn how it works

---

**Last updated**: October 2024  
**Version**: 1.0.0  
**Status**: Complete & Ready

**Next**: Choose your path above and start building! 🚀
