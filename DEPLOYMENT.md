# Deployment Guide

## Quick Deploy Options

### 1. **Netlify (Easiest - Static Version)**
1. Go to [netlify.com](https://netlify.com)
2. Drag & drop your project folder
3. Your link: `https://your-site-name.netlify.app`

### 2. **Vercel (Full-Stack)**
1. Go to [vercel.com](https://vercel.com)
2. Import from GitHub or upload folder
3. Your link: `https://your-project.vercel.app`

### 3. **Render (Database Support)**
1. Go to [render.com](https://render.com)
2. Connect GitHub repository
3. Your link: `https://your-app.onrender.com`

### 4. **GitHub Pages (Free Static)**
1. Upload to GitHub repository
2. Go to Settings > Pages
3. Select source branch
4. Your link: `https://username.github.io/repository-name`

## Files Created for Deployment:
- `deploy-simple.html` - Static version (no database)
- `vercel.json` - Vercel configuration
- `netlify.toml` - Netlify configuration  
- `render.yaml` - Render configuration
- `.gitignore` - Git ignore file

## Recommended: Netlify for Quick Demo
1. Rename `deploy-simple.html` to `index.html`
2. Upload to Netlify
3. Get instant live link!

## Your Live Link Will Be:
- **Netlify**: `https://car-rental-[random].netlify.app`
- **Vercel**: `https://car-rental-[random].vercel.app`
- **Render**: `https://car-rental-[random].onrender.com`