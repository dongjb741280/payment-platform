#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PDF to Markdown Converter with Images
批量将PDF文件转换为Markdown格式，包含图片提取
"""

import os
import re
import sys
from pathlib import Path
import subprocess
import argparse
import fitz  # PyMuPDF
from PIL import Image
import io
import shutil

def clean_filename(filename):
    """清理文件名，保持原有编号格式并添加描述性内容"""
    # 移除版本号和时间戳
    filename = re.sub(r'_V\d{8}', '', filename)
    filename = re.sub(r'\.pdf$', '', filename, flags=re.IGNORECASE)
    
    # 提取编号部分
    number_match = re.match(r'^(\d+\.)', filename)
    if number_match:
        number_part = number_match.group(1)
        # 获取描述性内容（编号后的部分）
        description = filename[len(number_part):].strip()
        
        # 清理描述性内容中的特殊字符
        description = description.replace('：', '_')
        description = description.replace('（', '(')
        description = description.replace('）', ')')
        description = description.replace('《', '')
        description = description.replace('》', '')
        
        # 移除多余的空格和特殊字符
        description = re.sub(r'[^\w\s\-_()]', '', description)
        description = re.sub(r'\s+', '_', description)
        description = description.strip('_')
        
        # 组合编号和描述
        if description:
            filename = f"{number_part}{description}"
        else:
            filename = number_part
    
    return filename

def extract_images_from_pdf(pdf_path, output_dir, exclude_last_two_pages=True):
    """从PDF中提取图片"""
    try:
        # 创建图片目录
        images_dir = output_dir / "images"
        images_dir.mkdir(exist_ok=True)
        
        # 打开PDF文件
        pdf_document = fitz.open(pdf_path)
        images = []
        all_images = []  # 临时存储所有图片信息，用于后续过滤
        
        for page_num in range(len(pdf_document)):
            page = pdf_document[page_num]
            
            # 获取页面上的图片
            image_list = page.get_images()
            
            for img_index, img in enumerate(image_list):
                try:
                    # 获取图片数据
                    xref = img[0]
                    pix = fitz.Pixmap(pdf_document, xref)
                    
                    if pix.n - pix.alpha < 4:  # 确保图片不是alpha通道
                        # 生成图片文件名
                        base_name = clean_filename(Path(pdf_path).stem)
                        img_filename = f"{base_name}_page_{page_num + 1}_img_{img_index + 1}.png"
                        img_path = images_dir / img_filename
                        
                        # 记录图片信息（先不保存，等过滤后再保存）
                        image_info = {
                            'page': page_num + 1,
                            'index': img_index + 1,
                            'filename': img_filename,
                            'path': img_path,
                            'pixmap': pix
                        }
                        all_images.append(image_info)
                    
                    pix = None  # 释放内存
                    
                except Exception as e:
                    print(f"  提取图片失败: {e}")
                    continue
        
        pdf_document.close()
        
        # 过滤最后两页的图片
        if exclude_last_two_pages and all_images:
            # 找到最大页码
            max_page = max(img['page'] for img in all_images)
            
            # 过滤掉最后两页的图片
            filtered_images = []
            for img_info in all_images:
                if img_info['page'] < max_page - 1:  # 保留除最后两页外的所有图片
                    # 保存图片
                    img_info['pixmap'].save(str(img_info['path']))
                    
                    # 移除pixmap引用，避免内存泄漏
                    pixmap = img_info.pop('pixmap')
                    pixmap = None
                    
                    images.append(img_info)
                    print(f"  提取图片: {img_info['filename']}")
                else:
                    print(f"  跳过最后两页图片: {img_info['filename']}")
                    # 释放pixmap内存
                    img_info['pixmap'] = None
        else:
            # 不过滤，保存所有图片
            for img_info in all_images:
                img_info['pixmap'].save(str(img_info['path']))
                pixmap = img_info.pop('pixmap')
                pixmap = None
                images.append(img_info)
                print(f"  提取图片: {img_info['filename']}")
        
        return images
        
    except Exception as e:
        print(f"提取图片时出错: {e}")
        return []

def convert_pdf_to_markdown_with_images(pdf_path, output_dir, extract_tables=True, keep_table_text=True, exclude_last_two_pages=True):
    """转换单个PDF文件为Markdown，包含图片"""
    try:
        # 获取文件名（不含扩展名）
        pdf_name = Path(pdf_path).stem
        clean_name = clean_filename(pdf_name)
        
        # 输出文件路径
        output_path = Path(output_dir) / f"{clean_name}.md"
        
        print(f"正在转换: {pdf_path}")
        print(f"输出到: {output_path}")
        
        # 提取图片
        print("  提取图片...")
        images = extract_images_from_pdf(pdf_path, output_dir, exclude_last_two_pages)
        
        # 提取文本内容和表格
        print("  提取文本和表格...")
        try:
            import pdfplumber
            with pdfplumber.open(pdf_path) as pdf:
                text = ""
                tables_data = []
                for page_num, page in enumerate(pdf.pages):
                    # 先提取表格，用于后续排除重复内容
                    page_tables = []
                    if extract_tables:
                        page_tables = page.extract_tables()
                        if page_tables:
                            for table_idx, table in enumerate(page_tables):
                                # 检查表格是否有效（至少有一行且不为空）
                                if table and len(table) > 0:
                                    # 检查表格是否有实际内容（不是全空行）
                                    has_content = False
                                    valid_rows = 0
                                    for row in table:
                                        if row and any(cell and str(cell).strip() for cell in row):
                                            has_content = True
                                            valid_rows += 1
                                    
                                    # 更严格的表格验证：
                                    # 1. 必须有实际内容
                                    # 2. 至少要有2行有效数据（避免单行被误认为表格）
                                    # 3. 检查是否有明显的表格结构（多列）
                                    if has_content and valid_rows >= 2:
                                        # 检查是否有明显的多列结构
                                        max_cols = 0
                                        for row in table:
                                            if row:
                                                non_empty_cells = sum(1 for cell in row if cell and str(cell).strip())
                                                max_cols = max(max_cols, non_empty_cells)
                                        
                                        # 只有当有多列结构时才认为是真正的表格
                                        if max_cols >= 2:
                                            tables_data.append({
                                                'page': page_num + 1,
                                                'index': table_idx + 1,
                                                'table': table
                                            })
                    
                    # 提取文本内容
                    page_text = page.extract_text()
                    if page_text:
                        # 如果有表格，尝试从文本中排除表格内容
                        if page_tables and tables_data:
                            # 获取当前页面的表格
                            current_page_tables = [t for t in tables_data if t['page'] == page_num + 1]
                            if current_page_tables:
                                # 从文本中移除表格内容（简化处理：移除包含表格关键词的行）
                                text_lines = page_text.split('\n')
                                filtered_lines = []
                                for line in text_lines:
                                    line = line.strip()
                                    # 跳过空行
                                    if not line:
                                        continue
                                    # 检查这行是否可能是表格内容
                                    is_table_content = False
                                    for table_info in current_page_tables:
                                        for row in table_info['table']:
                                            for cell in row:
                                                if cell and str(cell).strip() in line:
                                                    is_table_content = True
                                                    break
                                            if is_table_content:
                                                break
                                        if is_table_content:
                                            break
                                    
                                    if not is_table_content:
                                        filtered_lines.append(line)
                                
                                page_text = '\n'.join(filtered_lines)
                        
                        if page_text.strip():
                            text += f"\n\n--- 第 {page_num + 1} 页 ---\n\n"
                            text += page_text
        except ImportError:
            try:
                import PyPDF2
                tables_data = []  # PyPDF2不支持表格提取，设为空列表
                with open(pdf_path, 'rb') as file:
                    reader = PyPDF2.PdfReader(file)
                    for page_num, page in enumerate(reader.pages):
                        page_text = page.extract_text()
                        if page_text:
                            text += f"\n\n--- 第 {page_num + 1} 页 ---\n\n"
                            text += page_text
            except ImportError:
                print("  未安装PDF处理库，请安装: pip install pdfplumber PyPDF2")
                return False
        
        # 转换为Markdown格式
        markdown_content = convert_text_to_markdown_with_images(text, clean_name, images, tables_data, keep_table_text)
        
        # 写入Markdown文件
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(markdown_content)
        
        print(f"✓ 转换完成: {output_path}")
        print(f"  提取图片数量: {len(images)}")
        return True
        
    except Exception as e:
        print(f"✗ 转换过程中出错: {e}")
        return False

def convert_text_to_markdown_with_images(text, title, images, tables_data=None, keep_table_text=True):
    """将文本转换为Markdown格式，包含图片引用和表格"""
    
    def table_to_markdown(table):
        """将表格数据转换为Markdown表格格式"""
        if not table or not table[0]:
            return ""
        
        markdown_table = ""
        for i, row in enumerate(table):
            if not row or all(cell is None or cell == '' for cell in row):
                continue
                
            # 清理单元格内容（去除文字，只保留表格结构）
            cleaned_row = []
            for cell in row:
                if cell is None:
                    cleaned_row.append("")
                else:
                    # 检查单元格是否有内容
                    cell_content = str(cell).replace('\n', ' ').strip()
                    if cell_content:
                        if keep_table_text:
                            # 保留文字内容
                            cleaned_row.append(cell_content)
                        else:
                            # 将文字内容替换为空或占位符
                            cleaned_row.append("")  # 或者使用 "---" 作为占位符
                    else:
                        cleaned_row.append("")
            
            # 添加表格行
            markdown_table += "| " + " | ".join(cleaned_row) + " |\n"
            
            # 添加表头分隔线（第一行后）
            if i == 0:
                separator = "| " + " | ".join(["---"] * len(cleaned_row)) + " |\n"
                markdown_table += separator
        
        return markdown_table + "\n"
    
    def is_page_number_line(raw_line: str) -> bool:
        """判断一行是否为页码行，尽量不影响正常编号标题或列表。

        规则（任一命中则判定为页码行）:
        - 仅由1-4位数字组成（如: 1, 12, 123, 202）
        - 被少量破折号/短横线包裹的数字（如: - 1 -, — 2 —, -- 3 --）
        - 比例式页码（如: 3/24, 10 / 120）
        - “第X页/第 X 页/第X/YY页”等中文页码（如: 第3页, 第 10 / 120 页）
        - 常见页眉/页脚合并格式（如: Page 3 of 24）
        """
        line = raw_line.strip()
        if not line:
            return False

        # 仅纯数字（避免误伤带点/括号的编号）
        if re.fullmatch(r"\d{1,4}", line):
            return True

        # - 1 -、— 2 —、-- 3 -- 等
        if re.fullmatch(r"[\-–—\s]*\d{1,4}[\-–—\s]*", line):
            # 为避免把诸如 "- 项目 -" 误判，这里要求至少有数字且总长很短
            digits = re.sub(r"\D", "", line)
            non_digits = re.sub(r"\d", "", line)
            if 1 <= len(digits) <= 4 and len(line) <= 10 and non_digits.strip() in {"", "-", "–", "—", "--"}:
                return True

        # 3/24 或 10 / 120
        if re.fullmatch(r"\d{1,4}\s*/\s*\d{1,4}", line):
            return True

        # 第3页 / 第 10 / 120 页
        if re.fullmatch(r"第\s*\d{1,4}(\s*/\s*\d{1,4})?\s*页", line):
            return True

        # Page 3 of 24 / Page 3
        if re.fullmatch(r"(?i)page\s*\d{1,4}(\s*of\s*\d{1,4})?", line):
            return True

        return False
    # 添加标题
    markdown = f"# {title}\n\n"
    
    # 处理文本内容
    lines = text.split('\n')
    current_page = 1
    pending_line = None  # 缓存上一条非空、非特殊行，便于与编号行合并
    
    skip_next_line = False
    for i, line in enumerate(lines):
        if skip_next_line:
            skip_next_line = False
            continue
        line = line.strip()
        if line:
            # 去除独立页码行
            if is_page_number_line(line):
                continue
            # 检测页面分隔符
            if line.startswith('--- 第') and line.endswith('页 ---'):
                page_match = re.search(r'第 (\d+) 页', line)
                if page_match:
                    current_page = int(page_match.group(1))
                    # markdown += f"\n## 📄 第 {current_page} 页\n\n"
                    
                    # 插入该页的图片
                    page_images = [img for img in images if img['page'] == current_page]
                    if page_images:
                        # markdown += "### 页面图片\n\n"
                        for img in page_images:
                            markdown += f"![图片{img['index']}](./images/{img['filename']})\n\n"
                        markdown += "---\n\n"
                    
                    # 插入该页的表格
                    if tables_data:
                        page_tables = [table for table in tables_data if table['page'] == current_page]
                        if page_tables:
                            for table_info in page_tables:
                                table_markdown = table_to_markdown(table_info['table'])
                                if table_markdown.strip():
                                    # markdown += f"### 表格 {table_info['index']}\n\n"
                                    markdown += table_markdown
                                    markdown += "---\n\n"
                    
                    # 新页面时清空缓冲，避免跨页合并
                    pending_line = None
                    continue
            
            # 合并两行项目符号列表：形如 "• 标题" + 下一行描述 => "- 标题 描述"
            # 支持符号：•、-、·、*（避免与乘号或数学表达式冲突，仅在标题行短且下一行非列表时触发）
            bullet_match = re.fullmatch(r"[•\-·*]\s*(\S[^。！？；;:]*)\s*", line)
            if bullet_match and (i + 1) < len(lines):
                candidate = bullet_match.group(1).strip()
                next_line = lines[i + 1].strip()
                # 下一行必须是正文，不是页码/分隔符/另一个列表项/空行
                if next_line and not is_page_number_line(next_line) and not next_line.startswith('--- 第') and not re.match(r"^[•\-·*]\s+", next_line):
                    # 刷新未输出段落
                    if pending_line:
                        markdown += f"{pending_line}\n\n"
                        pending_line = None
                    markdown += f"- {candidate} {next_line}\n\n"
                    # 跳过下一行，避免重复
                    # 通过在循环尾部继续来跳过，下方设置一个轻量标记方式：直接将下一行置空
                    lines[i + 1] = ""
                    continue

            # 检测是否为标题格式的"1." - 当该行单独成行且下一行是文本时，与下一行合并为同一标题行
            if re.fullmatch(r"\d+\.", line):
                # 检查下一行是否有内容
                if i + 1 < len(lines):
                    next_line = lines[i + 1].strip()
                    # 下一行有内容且不是页码、不是页面分隔符，则认为是标题
                    if next_line and not is_page_number_line(next_line) and not next_line.startswith('--- 第'):
                        # 在输出合并标题前，先刷出未输出段落
                        if pending_line:
                            markdown += f"{pending_line}\n\n"
                            pending_line = None
                        markdown += f"## {line} {next_line}\n\n"
                        skip_next_line = True
                        continue
                # 如果没有下一行，则当作普通文字处理
                # 继续到下面的普通文字处理逻辑

            # 检测完整的"1. 标题文本"格式 - 只有当文本较短且不包含句号时才认为是标题
            if re.fullmatch(r"\d+\.\s+.+", line):
                title_match = re.match(r"(\d+\.)\s+(.+)", line)
                if title_match:
                    title_text = title_match.group(2).strip()
                    
                    # 简单判断：文本较短且不包含句号、问号、感叹号
                    is_title = (
                        len(title_text) <= 10 and  # 更严格的长度限制
                        not re.search(r'[。！？]', title_text) and  # 不包含句号、感叹号、问号
                        not title_text.endswith('.')  # 不以英文句号结尾
                    )
                    
                    if is_title:
                        # 写入前若有未刷新的缓冲段落，先输出
                        if pending_line:
                            markdown += f"{pending_line}\n\n"
                            pending_line = None
                        markdown += f"## {line}\n\n"
                        continue
                # 如果不满足标题条件，则当作普通文字处理

            # 若本行形如"1.1."编号行，优先与下一行合并为同一标题行
            if re.fullmatch(r"\d+\.\d+\.", line):
                if i + 1 < len(lines):
                    next_line = lines[i + 1].strip()
                    if next_line and not is_page_number_line(next_line) and not next_line.startswith('--- 第'):
                        if pending_line:
                            markdown += f"{pending_line}\n\n"
                            pending_line = None
                        markdown += f"### {line} {next_line}\n\n"
                        skip_next_line = True
                        continue
                # 否则，若有上一行缓冲，则与缓冲合并；没有则独立输出
                if pending_line:
                    markdown += f"### {line} {pending_line}\n\n"
                    pending_line = None
                else:
                    markdown += f"### {line}\n\n"
                continue

            # 若本行已是"1.1. 标题文本"整体行，则直接作为标题
            if re.fullmatch(r"\d+\.\d+\.\s+.+", line):
                # 写入前若有未刷新的缓冲段落，先输出
                if pending_line:
                    markdown += f"{pending_line}\n\n"
                    pending_line = None
                markdown += f"### {line}\n\n"
                continue

            # 到这里为普通文本。若已有缓冲，先刷出上一段再缓存当前行
            if pending_line:
                markdown += f"{pending_line}\n\n"
            pending_line = line
            continue

    # 循环结束后，如仍有缓冲的上一行，作为段落输出
    if pending_line:
        markdown += f"{pending_line}\n\n"
    
    return markdown

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description='批量转换PDF文件为Markdown（包含图片）')
    parser.add_argument('--input-dir', default='.', help='输入目录（默认当前目录）')
    parser.add_argument('--output-dir', default='markdown_docs_with_images', help='输出目录（默认markdown_docs_with_images）')
    parser.add_argument('--no-tables', action='store_true', help='不提取表格（避免误识别普通文本为表格）')
    parser.add_argument('--include-last-pages', action='store_true', help='包含最后两页的图片（默认排除最后两页的图片）')
    
    args = parser.parse_args()
    
    input_dir = Path(args.input_dir)
    output_dir = Path(args.output_dir)
    
    # 先清空输出目录（若存在）再创建
    if output_dir.exists() and output_dir.is_dir():
        shutil.rmtree(output_dir)
    output_dir.mkdir(exist_ok=True)
    
    # 查找所有PDF文件
    pdf_files = list(input_dir.glob('*.pdf'))
    
    if not pdf_files:
        print("未找到PDF文件")
        return
    
    print(f"找到 {len(pdf_files)} 个PDF文件")
    print(f"输出目录: {output_dir}")
    print("-" * 50)
    
    success_count = 0
    failed_files = []
    total_images = 0
    
    for pdf_file in sorted(pdf_files):
        extract_tables = not args.no_tables
        exclude_last_two_pages = not args.include_last_pages
        if convert_pdf_to_markdown_with_images(pdf_file, output_dir, extract_tables, exclude_last_two_pages=exclude_last_two_pages):
            success_count += 1
            # 统计图片数量
            images_dir = output_dir / "images"
            if images_dir.exists():
                page_images = len([f for f in images_dir.glob(f"{clean_filename(pdf_file.stem)}_*.png")])
                total_images += page_images
        else:
            failed_files.append(pdf_file.name)
    
    print("-" * 50)
    print(f"转换完成！成功: {success_count}/{len(pdf_files)}")
    print(f"总提取图片数: {total_images}")
    
    if failed_files:
        print("转换失败的文件:")
        for file in failed_files:
            print(f"  - {file}")

if __name__ == "__main__":
    main()
