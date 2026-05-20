# OOP
My First OOP Project 
# 🏦 Banking Management System

نظام إدارة بنكي متكامل تم تطويره باستخدام لغة Java وتطبيق مفاهيم البرمجة كائنية التوجه (OOP). يهدف النظام إلى محاكاة العمليات البنكية الأساسية مع توفير نظام صلاحيات للمستخدمين وضمان استمرارية البيانات (Data Persistence).

## 🚀 المميزات الرئيسية
* **نظام مصادقة (Authentication):** تسجيل دخول آمن مع صلاحيات مختلفة (Customer, Employee, Admin).
* **إدارة الحسابات:** دعم أنواع الحسابات (Checking & Savings) مع قيود مختلفة لكل نوع.
* **العمليات المالية:** تنفيذ عمليات الإيداع، السحب، والتحويل بين الحسابات.
* **تخزين البيانات:** نظام حفظ واسترجاع للبيانات باستخدام الملفات النصية (.txt) لضمان عدم فقدان البيانات.
* **واجهة رسومية (GUI):** واجهة مستخدم سهلة مبنية باستخدام Java Swing.

## 🛠️ التقنيات المستخدمة
- **Language:** Java
- **UI:** Java Swing (CardLayout)
- **Data Storage:** File I/O (CSV format)
- **Design Pattern:** Object-Oriented Programming (OOP)

## 🏗️ هيكل المشروع
- `Main.java`: نقطة الدخول وواجهة المستخدم.
- `Bank.java`: المحرك الأساسي لإدارة النظام والعمليات.
- `Account.java`: كلاس مجرد (Abstract) يحدد خصائص الحسابات.
- `FileManager.java`: المسؤول عن قراءة وكتابة البيانات في الملفات.
- `User` & `Customer` & `Employee` & `Admin`: هيكل المستخدمين والصلاحيات.

## ⚙️ كيفية التشغيل
1. تأكد من تثبيت [Java JDK](https://www.oracle.com/java/technologies/downloads/).
2. قم بتحميل المشروع أو عمل Clone للمستودع.
3. افتح المشروع في بيئة التطوير (IDE) المفضلة لديك (مثل IntelliJ IDEA أو Eclipse).
4. قم بتشغيل كلاس `Main.java`.

##📸 لقطات من المشروع
 <img width="1548" height="798" alt="Screenshot 2026-05-20 230535" src="https://github.com/user-attachments/assets/8b94eaf8-5070-43e9-b413-bc24a658e8b7" />

## 🤝 المساهمة
هذا المشروع جزء من متطلبات مقرر CSE015. إذا كان لديك أي ملاحظات أو اقتراحات لتطوير النظام، لا تتردد في التواصل معي.

---
**تم التطوير بواسطة: [ُENG:Mohamed Amr]**
