import fs from 'fs';
import path from 'path';
import { NextResponse } from 'next/server';

const CONTENT_TYPES = {
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.png': 'image/png',
  '.gif': 'image/gif',
  '.webp': 'image/webp',
  '.bmp': 'image/bmp',
  '.svg': 'image/svg+xml',
  '.pdf': 'application/pdf',
  '.doc': 'application/msword',
  '.docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  '.txt': 'text/plain',
  '.json': 'application/json',
  '.xml': 'application/xml',
  '.zip': 'application/zip',
  '.mp4': 'video/mp4',
  '.mp3': 'audio/mpeg',
  '.wav': 'audio/wav',
  '.css': 'text/css',
  '.js': 'application/javascript',
  '.html': 'text/html'
};

const CACHE_HEADERS = {
  'Cache-Control': 'public, max-age=3600, must-revalidate',
  'Vary': 'Accept-Encoding'
};

function validateFilePath(filePath) {
  if (!filePath || typeof filePath !== 'string') return false;
  const normalized = path.normalize(filePath);
  return !normalized.includes('..') && !normalized.startsWith('/') && normalized.length > 0;
}

function getFileInfo(filePath) {
  try {
    const stats = fs.statSync(filePath);
    if (!stats.isFile()) return null;
    
    const fileName = path.basename(filePath);
    const ext = path.extname(fileName).toLowerCase();
    const contentType = CONTENT_TYPES[ext] || 'application/octet-stream';
    
    return { stats, fileName, contentType };
  } catch {
    return null;
  }
}

export async function GET(request) {
  try {
    const filePath = new URL(request.url).searchParams.get('path');
    
    if (!validateFilePath(filePath)) {
      return NextResponse.json({ error: 'Invalid file path' }, { status: 400 });
    }

    const fileInfo = getFileInfo(filePath);
    if (!fileInfo) {
      return NextResponse.json({ error: 'File not found' }, { status: 404 });
    }

    const { stats, fileName, contentType } = fileInfo;
    const fileBuffer = fs.readFileSync(filePath);

    return new Response(fileBuffer, {
      headers: {
        'Content-Type': contentType,
        'Content-Length': stats.size.toString(),
        'Content-Disposition': `inline; filename="${fileName}"`,
        'Last-Modified': stats.mtime.toUTCString(),
        'ETag': `"${stats.size}-${stats.mtime.getTime()}"`,
        ...CACHE_HEADERS
      }
    });

  } catch (error) {
    console.error('File server error:', error);
    return NextResponse.json({ error: 'Server error' }, { status: 500 });
  }
}

export default function handler(req, res) {
  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    const { path: filePath } = req.query;
    
    if (!validateFilePath(filePath)) {
      return res.status(400).json({ error: 'Invalid file path' });
    }

    const fileInfo = getFileInfo(filePath);
    if (!fileInfo) {
      return res.status(404).json({ error: 'File not found' });
    }

    const { stats, fileName, contentType } = fileInfo;
    const fileBuffer = fs.readFileSync(filePath);

    res.setHeader('Content-Type', contentType);
    res.setHeader('Content-Length', stats.size);
    res.setHeader('Content-Disposition', `inline; filename="${fileName}"`);
    res.setHeader('Last-Modified', stats.mtime.toUTCString());
    res.setHeader('ETag', `"${stats.size}-${stats.mtime.getTime()}"`);
    
    Object.entries(CACHE_HEADERS).forEach(([key, value]) => {
      res.setHeader(key, value);
    });
    
    res.send(fileBuffer);

  } catch (error) {
    console.error('File server error:', error);
    res.status(500).json({ error: 'Server error' });
  }
}