#!/bin/bash

# 数据库配置信息
DB_HOST="127.0.0.1"
DB_USER="wdclouduser"

export MYSQL_PWD='wdclouduser'


# SQL 文件所在目录
SQL_DIR_WD_PERMISSION="wd_permission"
SQL_DIR_WD_IPAAS="wd_ipaas"

# 检查 MySQL 客户端是否安装
if ! command -v mysql &> /dev/null
then
    echo "MySQL client could not be found. Please install it first."
    exit 1
fi

# 创建数据库函数
create_database() {
    local db_name=$1
    echo "Checking if database $db_name exists..."
    mysql -h "$DB_HOST" -u "$DB_USER"  -e "use \$db_name;"
    if [ $? -eq 0 ]; then
        echo "Database $db_name created or already exists."
    else
        echo "Failed to create database $db_name."
        exit 1
    fi
}

# 进入 SQL 文件目录并执行 SQL 文件
execute_sql_files() {
    local sql_dir=$1
    local db_name=$2

    cd "$sql_dir" || { echo "Directory $sql_dir does not exist."; exit 1; }


    # 执行目录下所有的 SQL 文件
    for sql_file in *.sql
    do
        echo "Executing $sql_file..."
        mysql -h "$DB_HOST" -u "$DB_USER" "$db_name" < "$sql_file"
        if [ $? -eq 0 ]; then
            echo "$sql_file executed successfully."
        else
            echo "Failed to execute $sql_file."
            exit 1
        fi
    done
}

# 执行 wd_permission 目录下的 SQL 文件
execute_sql_files "$SQL_DIR_WD_PERMISSION" "wd_permission"

cd ..

# 执行 wd_ipaas 目录下的 SQL 文件
execute_sql_files "$SQL_DIR_WD_IPAAS" "wd_ipaas"

echo "All SQL files executed successfully."