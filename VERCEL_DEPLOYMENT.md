# Vercel Deployment Guide

## Fixed Issues:

### 1. Car Images Not Appearing
- ✅ Moved images from `assets/` to `public/assets/`
- ✅ Updated image paths to use `/assets/` instead of `assets/`
- ✅ Updated vercel.json to serve static assets properly

### 2. Database Connection Issues
- ✅ Updated database.js to use environment variables
- ✅ Added SSL configuration for production
- ✅ Created .env.example for reference

## Deployment Steps:

### 1. Set up Database (Choose one):

**Option A: Vercel Postgres (Recommended)**
1. Go to your Vercel dashboard
2. Select your project
3. Go to Storage tab
4. Create a Postgres database
5. Copy the connection string

**Option B: External Database (Neon, Supabase, etc.)**
1. Create account on Neon.tech or Supabase
2. Create a new database
3. Copy the connection string

### 2. Configure Environment Variables:
1. In Vercel dashboard, go to Settings > Environment Variables
2. Add: `DATABASE_URL` = your_connection_string
3. Add: `POSTGRES_URL` = your_connection_string (same value)

### 3. Deploy:
```bash
# Install Vercel CLI if not installed
npm i -g vercel

# Deploy
vercel --prod
```

### 4. Initialize Database:
After deployment, run the SQL from `setup_database.sql` in your cloud database.

## Environment Variables Needed:
- `DATABASE_URL` - PostgreSQL connection string
- `POSTGRES_URL` - Same as DATABASE_URL (for compatibility)

## File Changes Made:
- ✅ `public/assets/` - Car images moved here
- ✅ `car-rental.html` - Updated image paths to `/assets/`
- ✅ `database.js` - Uses environment variables
- ✅ `server.js` - Serves public directory
- ✅ `vercel.json` - Updated routing for assets and API
- ✅ `.env.example` - Environment variables template

Your app should now work properly on Vercel with images displaying and login functionality working!