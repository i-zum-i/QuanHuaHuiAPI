#!/usr/bin/env python3
"""
Script to fix encoding issues in Java files.
This script will:
1. Read files with encoding detection
2. Fix common Japanese text corruption patterns
3. Convert files to UTF-8
"""

import os
import re
import chardet
import subprocess
import sys
from pathlib import Path

def detect_encoding(file_path):
    """Detect file encoding"""
    try:
        with open(file_path, 'rb') as f:
            raw_data = f.read()
            result = chardet.detect(raw_data)
            return result['encoding']
    except Exception as e:
        print(f"Error detecting encoding for {file_path}: {e}")
        return None

def fix_japanese_corruption(text):
    """Fix common Japanese text corruption patterns"""
    
    # Common corruption patterns and their fixes
    corruption_fixes = {
        # Configuration/設定 related
        'Jackson設定クラス': 'Jackson設定クラス',
        'チE��リアライゼーション': 'デシリアライゼーション',
        'チE��リアライゼーション': 'シリアライゼーション',
        'E��リアライゼーション': 'デシリアライゼーション',
        '管琁E��ます': '管理します',
        '管琁E��': '管理',
        '設宁E': '設定',
        'サポ�EチE': 'サポート',
        'E��E': 'します',
        '��E': 'を',
        '除夁E': '除外',
        
        # Entity/エンティティ related
        'エンチE��チE��': 'エンティティ',
        'エンチE��': 'エンティティ',
        'チE��': 'ティ',
        'E��': 'を',
        '��': 'し',
        
        # System/システム related
        'シスチE��': 'システム',
        'シスチE': 'システム',
        
        # Common endings and particles
        'E/p>': '</p>',
        'E��': 'を',
        '��': 'し',
        '、E': 'し',
        
        # Test related
        'チE��チE': 'テスト',
        'チE��': 'テスト',
        
        # Repository/リポジトリ related
        'リポジチE': 'リポジトリ',
        'リポジチE��': 'リポジトリ',
        
        # Service/サービス related
        'サ�E��ビス': 'サービス',
        'サ�E': 'サービス',
        
        # Other common patterns
        'E��する': 'を実行する',
        'E��行': 'を実行',
        'E��': 'を',
        '琁E': '理',
        '夁E': '外',
        '宁E': '定',
        '�E': 'ー',
        '��': 'し',
        '、E': 'し',
    }
    
    # Apply fixes
    fixed_text = text
    for corrupt, fix in corruption_fixes.items():
        fixed_text = fixed_text.replace(corrupt, fix)
    
    # Additional pattern-based fixes using regex
    # Fix corrupted katakana patterns
    fixed_text = re.sub(r'[E��]+', 'ー', fixed_text)
    
    return fixed_text

def fix_java_file(file_path):
    """Fix encoding issues in a Java file"""
    try:
        # Try to read with detected encoding
        encoding = detect_encoding(file_path)
        if not encoding:
            encoding = 'utf-8'
        
        print(f"Processing {file_path} with encoding {encoding}")
        
        # Read the file
        with open(file_path, 'r', encoding=encoding, errors='ignore') as f:
            content = f.read()
        
        # Fix Japanese corruption
        fixed_content = fix_japanese_corruption(content)
        
        # Ensure proper line endings (Unix style)
        fixed_content = fixed_content.replace('\r\n', '\n').replace('\r', '\n')
        
        # Write back as UTF-8
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(fixed_content)
        
        print(f"Fixed: {file_path}")
        return True
        
    except Exception as e:
        print(f"Error fixing {file_path}: {e}")
        return False

def main():
    """Main function to process all Java files"""
    root_dir = Path(__file__).parent
    java_files = list(root_dir.rglob("*.java"))
    
    print(f"Found {len(java_files)} Java files to process")
    
    success_count = 0
    for java_file in java_files:
        if fix_java_file(java_file):
            success_count += 1
    
    print(f"\nProcessed {success_count}/{len(java_files)} files successfully")

if __name__ == "__main__":
    main()