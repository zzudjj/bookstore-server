create table about
(
    id         int auto_increment comment '主键ID (固定为1)'
        primary key,
    content    text                                not null comment '网站介绍内容',
    updateTime timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间'
)
    comment '网站介绍信息表' charset = utf8mb4;

create table announcement
(
    id          int auto_increment comment '公告编号'
        primary key,
    title       varchar(255)                         not null comment '公告标题',
    content     text                                 not null comment '公告内容',
    author      varchar(100)                         null comment '发布人账号',
    publishTime timestamp  default CURRENT_TIMESTAMP null comment '发布时间',
    enable      tinyint(1) default 1                 null comment '是否展示 (1=展示,0=隐藏)'
)
    comment '网站公告表' charset = utf8mb4;

create index idx_enable
    on announcement (enable);

create table book
(
    id          int auto_increment comment '图书编号'
        primary key,
    bookName    varchar(255)         not null comment '图书名称',
    author      varchar(255)         null comment '作者',
    isbn        varchar(50)          not null comment 'ISBN号',
    publish     varchar(255)         null comment '出版社',
    birthday    timestamp            null comment '出版时间',
    marketPrice decimal(10, 2)       null comment '市场价',
    price       decimal(10, 2)       null comment '售价',
    stock       int        default 0 null comment '库存',
    description text                 null comment '图书描述',
    put         tinyint(1) default 1 null comment '是否上架',
    `rank`      int        default 0 null comment '权重值',
    newProduct  tinyint(1) default 0 null comment '是否新品',
    recommend   tinyint(1) default 0 null comment '是否推荐',
    constraint uk_isbn
        unique (isbn)
)
    comment '图书表' charset = utf8mb4;

create index idx_newProduct
    on book (newProduct);

create index idx_publish
    on book (publish);

create index idx_put
    on book (put);

create index idx_recommend
    on book (recommend);

create table bookimg
(
    id     int auto_increment comment '图片编号'
        primary key,
    isbn   varchar(50)          not null comment '图书ISBN',
    imgSrc varchar(255)         not null comment '图片路径',
    cover  tinyint(1) default 0 null comment '是否为封面',
    constraint fk_bookimg_book
        foreign key (isbn) references book (isbn)
            on delete cascade
)
    comment '图书图片表' charset = utf8mb4;

create index idx_cover
    on bookimg (cover);

create index idx_isbn
    on bookimg (isbn);

create table booksort
(
    id        int auto_increment comment '分类编号'
        primary key,
    sortName  varchar(100)              not null comment '分类名称',
    upperName varchar(100) default '无' null comment '上级分类名称',
    level     varchar(20)               null comment '分类级别',
    `rank`    int          default 0    null comment '排序权重'
)
    comment '图书分类表' charset = utf8mb4;

create index idx_level
    on booksort (level);

create index idx_upperName
    on booksort (upperName);

create table booksortlist
(
    bookSortId int not null comment '分类ID',
    bookId     int not null comment '图书ID',
    primary key (bookSortId, bookId),
    constraint fk_booksortlist_book
        foreign key (bookId) references book (id)
            on delete cascade,
    constraint fk_booksortlist_sort
        foreign key (bookSortId) references booksort (id)
            on delete cascade
)
    comment '图书分类关联表' charset = utf8mb4;

create index idx_bookId
    on booksortlist (bookId);

create table coupon_template
(
    id                  int auto_increment comment '优惠券模板ID'
        primary key,
    name                varchar(100)                             not null comment '优惠券名称',
    type                tinyint(1)                               not null comment '优惠券类型：1-满减券，2-折扣券',
    discount_value      decimal(10, 2)                           not null comment '折扣值（满减券为减免金额，折扣券为折扣百分比，如85表示8.5折）',
    min_order_amount    decimal(10, 2) default 0.00              null comment '最低消费金额',
    max_discount_amount decimal(10, 2)                           null comment '最大折扣金额（仅折扣券使用）',
    total_quantity      int                                      not null comment '发放总数量',
    used_quantity       int            default 0                 null comment '已使用数量',
    received_quantity   int            default 0                 null comment '已领取数量',
    per_user_limit      int            default 1                 null comment '每用户限领数量',
    valid_days          int                                      not null comment '有效天数（从领取日开始计算）',
    status              tinyint(1)     default 1                 null comment '状态：0-停用，1-启用',
    create_time         timestamp      default CURRENT_TIMESTAMP null comment '创建时间',
    update_time         timestamp      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '优惠券模板表' charset = utf8mb4;

create index idx_status
    on coupon_template (status);

create index idx_type
    on coupon_template (type);

create table newproduct
(
    id      int auto_increment comment '新品推荐编号'
        primary key,
    bookId  int                                 not null comment '图书ID',
    `rank`  int       default 0                 null comment '推荐权重',
    addTime timestamp default CURRENT_TIMESTAMP null comment '添加时间',
    constraint uk_bookId
        unique (bookId),
    constraint fk_newproduct_book
        foreign key (bookId) references book (id)
            on delete cascade
)
    comment '新品推荐表' charset = utf8mb4;

create index idx_addTime
    on newproduct (addTime);

create index idx_rank
    on newproduct (`rank`);

create table order_config
(
    id           int auto_increment
        primary key,
    config_key   varchar(50)                         not null comment '配置键',
    config_value varchar(255)                        not null comment '配置值',
    description  varchar(255)                        null comment '配置描述',
    create_time  timestamp default CURRENT_TIMESTAMP null,
    update_time  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint config_key
        unique (config_key)
)
    comment '订单配置表';

create table publish
(
    id          int auto_increment comment '出版社编号'
        primary key,
    name        varchar(255)         not null comment '出版社名称',
    showPublish tinyint(1) default 1 null comment '是否显示',
    `rank`      int        default 0 null comment '排序权重',
    num         int        default 0 null comment '图书数量',
    constraint uk_name
        unique (name)
)
    comment '出版社表' charset = utf8mb4;

create table recommend
(
    id      int auto_increment comment '推荐编号'
        primary key,
    bookId  int                                 not null comment '图书ID',
    `rank`  int       default 0                 null comment '推荐权重',
    addTime timestamp default CURRENT_TIMESTAMP null comment '添加时间',
    constraint uk_bookId
        unique (bookId),
    constraint fk_recommend_book
        foreign key (bookId) references book (id)
            on delete cascade
)
    comment '推荐图书表' charset = utf8mb4;

create index idx_addTime
    on recommend (addTime);

create index idx_rank
    on recommend (`rank`);

create table spikeactivity
(
    id           bigint auto_increment comment '活动ID'
        primary key,
    activityName varchar(100)                         not null comment '活动名称',
    activityDesc text                                 null comment '活动描述',
    startTime    datetime                             not null comment '开始时间',
    endTime      datetime                             not null comment '结束时间',
    status       tinyint(1) default 0                 not null comment '状态：0-未开始，1-进行中，2-已结束，3-已取消',
    createTime   timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    createdBy    varchar(100)                         null comment '创建人账号'
)
    comment '秒杀活动表' charset = utf8mb4;

create index idx_createdBy
    on spikeactivity (createdBy);

create index idx_startEndTime
    on spikeactivity (startTime, endTime);

create index idx_status
    on spikeactivity (status);

create table spikegoods
(
    id            bigint auto_increment comment '秒杀商品ID'
        primary key,
    activityId    bigint                               not null comment '活动ID',
    bookId        int                                  not null comment '图书ID',
    spikePrice    decimal(10, 2)                       not null comment '秒杀价格',
    originalPrice decimal(10, 2)                       not null comment '原价',
    spikeStock    int                                  not null comment '秒杀库存',
    soldCount     int        default 0                 null comment '已售数量',
    limitPerUser  int        default 1                 null comment '每人限购数量',
    sortOrder     int        default 0                 null comment '排序',
    status        tinyint(1) default 1                 not null comment '状态：0-下架，1-上架',
    createTime    timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime    timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_spikeGoods_activity
        foreign key (activityId) references spikeactivity (id)
            on delete cascade,
    constraint fk_spikeGoods_book
        foreign key (bookId) references book (id)
            on delete cascade
)
    comment '秒杀商品表' charset = utf8mb4;

create index idx_activityId
    on spikegoods (activityId);

create index idx_bookId
    on spikegoods (bookId);

create index idx_sortOrder
    on spikegoods (sortOrder);

create index idx_status
    on spikegoods (status);

create table stock_reservation
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL COMMENT '图书ID',
    order_id VARCHAR(50) NOT NULL COMMENT '订单ID',
    reserved_quantity INT NOT NULL COMMENT '预留数量',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0-预留中，1-已确认，2-已释放',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
    COMMENT '库存预留表';

create index idx_book_id
    on stock_reservation (book_id);

create index idx_expire_time
    on stock_reservation (expire_time);

create index idx_order_id
    on stock_reservation (order_id);

create index idx_status
    on stock_reservation (status);

create table topic
(
    id        int auto_increment comment '书单ID'
        primary key,
    title     varchar(255)                         not null comment '书单标题',
    subTitle  varchar(255)                         null comment '副标题',
    cover     varchar(1024)                        null comment '封面图片',
    `rank`    int        default 0                 null comment '排序权重',
    status    tinyint(1) default 1                 null comment '是否上架(1=上架)',
    viewCnt   int        default 0                 null comment '浏览量',
    favCnt    int        default 0                 null comment '收藏量',
    orderCnt  int        default 0                 null comment '成交量',
    createdAt timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updatedAt timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '书单表' charset = utf8mb4;

create table topic_fav
(
    userAccount varchar(100)                        not null comment '用户账号',
    topicId     int                                 not null comment '书单ID',
    favAt       timestamp default CURRENT_TIMESTAMP null comment '收藏时间',
    primary key (userAccount, topicId)
)
    comment '书单收藏表' charset = utf8mb4;

create table topic_item
(
    topicId         int           not null comment '书单ID',
    bookId          int           not null comment '图书ID',
    recommendReason text          null comment '推荐理由',
    orderNo         int default 0 null comment '条目排序',
    primary key (topicId, bookId)
)
    comment '书单条目表' charset = utf8mb4;

create index idx_bookId
    on topic_item (bookId);

create table user
(
    id           int auto_increment comment '用户编号'
        primary key,
    account      varchar(100)                         not null comment '用户账号(邮箱)',
    password     varchar(255)                         not null comment '密码',
    name         varchar(50)                          null comment '用户姓名',
    gender       varchar(10)                          null comment '性别',
    imgUrl       varchar(255)                         null comment '头像URL',
    info         text                                 null comment '个人简介',
    manage       tinyint(1) default 0                 null comment '是否为管理员',
    enable       tinyint(1) default 1                 null comment '是否启用',
    registerTime timestamp  default CURRENT_TIMESTAMP null comment '注册时间',
    constraint uk_account
        unique (account)
)
    comment '用户表' charset = utf8mb4;

create table address
(
    id      int auto_increment comment '地址编号'
        primary key,
    account varchar(100)         not null comment '用户账号',
    name    varchar(50)          not null comment '收货人姓名',
    phone   varchar(20)          not null comment '收货人电话',
    addr    varchar(255)         not null comment '具体地址',
    label   varchar(50)          null comment '地址标签',
    off     tinyint(1) default 0 null comment '是否删除',
    constraint fk_address_user
        foreign key (account) references user (account)
            on delete cascade
)
    comment '收货地址表' charset = utf8mb4;

create index idx_account
    on address (account);

create table book_comment
(
    id         int auto_increment comment '评论编号'
        primary key,
    bookId     int                                 not null comment '图书ID',
    userId     int                                 not null comment '用户ID',
    parentId   int                                 null comment '父评论ID（用于二级评论）',
    content    text                                not null comment '评论内容',
    likeCount  int       default 0                 null comment '点赞数',
    createTime timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    constraint fk_comment_book
        foreign key (bookId) references book (id)
            on delete cascade,
    constraint fk_comment_parent
        foreign key (parentId) references book_comment (id)
            on delete cascade,
    constraint fk_comment_user
        foreign key (userId) references user (id)
            on delete cascade
)
    comment '图书评价表' charset = utf8mb4;

create index idx_bookId
    on book_comment (bookId);

create index idx_createTime
    on book_comment (createTime);

create index idx_likeCount
    on book_comment (likeCount);

create index idx_parentId
    on book_comment (parentId);

create index idx_userId
    on book_comment (userId);

create table bookorder
(
    id               int auto_increment comment '订单编号'
        primary key,
    orderId          varchar(50)                           not null comment '订单号',
    account          varchar(100)                          not null comment '用户账号',
    addressId        int                                   not null comment '收货地址ID',
    orderTime        timestamp   default CURRENT_TIMESTAMP null comment '下单时间',
    shipTime         timestamp                             null comment '发货时间',
    getTime          timestamp                             null comment '收货时间',
    evaluateTime     timestamp                             null comment '评价时间',
    closeTime        timestamp                             null comment '关闭时间',
    confirmTime      timestamp                             null comment '确认收货时间',
    orderStatus      varchar(50) default '待付款'          null comment '订单状态',
    logisticsCompany int                                   null comment '物流公司ID',
    logisticsNum     varchar(100)                          null comment '物流单号',
    beUserDelete     tinyint(1)  default 0                 null comment '用户是否删除',
    coupon_id        int                                   null comment '使用的优惠券ID',
    paymentDeadline  timestamp                             null comment '鏀?粯鎴??鏃堕棿',
    cancelReason     varchar(255)                          null comment '鍙栨秷鍘熷洜',
    operator         varchar(50)                           null comment '鏈?悗鎿嶄綔浜',
    constraint uk_orderId
        unique (orderId),
    constraint fk_bookorder_address
        foreign key (addressId) references address (id),
    constraint fk_bookorder_user
        foreign key (account) references user (account)
            on delete cascade
)
    comment '订单表' charset = utf8mb4;

create index idx_account
    on bookorder (account);

create index idx_account_status
    on bookorder (account, orderStatus);

create index idx_orderStatus
    on bookorder (orderStatus);

create index idx_order_status
    on bookorder (orderStatus);

create index idx_order_time
    on bookorder (orderTime);

create table cart
(
    account varchar(100)                        not null comment '用户账号',
    id      int                                 not null comment '图书ID',
    num     int       default 1                 null comment '数量',
    addTime timestamp default CURRENT_TIMESTAMP null comment '添加时间',
    primary key (account, id),
    constraint fk_cart_book
        foreign key (id) references book (id)
            on delete cascade,
    constraint fk_cart_user
        foreign key (account) references user (account)
            on delete cascade
)
    comment '购物车表' charset = utf8mb4;

create index idx_account
    on cart (account);

create table comment_like
(
    id        int auto_increment comment '点赞记录编号'
        primary key,
    commentId int not null comment '评论ID',
    userId    int not null comment '用户ID',
    constraint uk_comment_user
        unique (commentId, userId),
    constraint fk_like_comment
        foreign key (commentId) references book_comment (id)
            on delete cascade,
    constraint fk_like_user
        foreign key (userId) references user (id)
            on delete cascade
)
    comment '评论点赞记录表' charset = utf8mb4;

create index idx_userId
    on comment_like (userId);

create table expense
(
    orderId           varchar(50)                 not null comment '订单号'
        primary key,
    productTotalMoney decimal(10, 2) default 0.00 null comment '商品总价',
    freight           decimal(10, 2) default 0.00 null comment '运费',
    coupon            int            default 0    null comment '优惠券',
    coupon_id         int                         null comment '使用的优惠券ID',
    coupon_discount   decimal(10, 2) default 0.00 null comment '优惠券折扣金额',
    activityDiscount  decimal(10, 2) default 0.00 null comment '活动优惠',
    allPrice          decimal(10, 2) default 0.00 null comment '订单总金额',
    finallyPrice      decimal(10, 2) default 0.00 null comment '最终实付金额',
    constraint fk_expense_order
        foreign key (orderId) references bookorder (orderId)
            on delete cascade
)
    comment '订单费用表' charset = utf8mb4;

create index idx_coupon_id
    on expense (coupon_id);

create table orderdetail
(
    orderId varchar(50)    not null comment '订单号',
    bookId  int            not null comment '图书ID',
    num     int            not null comment '购买数量',
    price   decimal(10, 2) not null comment '购买时单价',
    primary key (orderId, bookId),
    constraint fk_orderdetail_book
        foreign key (bookId) references book (id),
    constraint fk_orderdetail_order
        foreign key (orderId) references bookorder (orderId)
            on delete cascade
)
    comment '订单明细表' charset = utf8mb4;

create index idx_bookId
    on orderdetail (bookId);

create table spikerecord
(
    id           bigint auto_increment comment '记录ID'
        primary key,
    spikeGoodsId bigint        not null comment '秒杀商品ID',
    userAccount  varchar(100)  not null comment '用户账号',
    quantity     int default 1 not null comment '购买数量',
    spikeTime    datetime      not null comment '秒杀时间',
    result       tinyint(1)    not null comment '结果：0-失败，1-成功',
    failReason   varchar(200)  null comment '失败原因',
    ipAddress    varchar(45)   null comment 'IP地址',
    userAgent    varchar(500)  null comment '用户代理',
    constraint fk_spikeRecord_goods
        foreign key (spikeGoodsId) references spikegoods (id)
            on delete cascade,
    constraint fk_spikeRecord_user
        foreign key (userAccount) references user (account)
            on delete cascade
)
    comment '秒杀记录表' charset = utf8mb4;

create index idx_result
    on spikerecord (result);

create index idx_spikeGoodsUser
    on spikerecord (spikeGoodsId, userAccount);

create index idx_spikeTime
    on spikerecord (spikeTime);

create index idx_userAccount
    on spikerecord (userAccount);

create table user_coupon
(
    id                 int auto_increment comment '用户优惠券ID'
        primary key,
    coupon_template_id int                                      not null comment '优惠券模板ID',
    account            varchar(100)                             not null comment '用户账号',
    coupon_code        varchar(50)                              not null comment '优惠券码（自动生成）',
    status             tinyint        default 1                 null comment '状态：1-未使用，2-已使用，3-已过期',
    receive_time       timestamp      default CURRENT_TIMESTAMP null comment '领取时间',
    use_time           timestamp                                null comment '使用时间',
    order_id           varchar(50)                              null comment '使用的订单号',
    expire_time        timestamp                                not null comment '过期时间',
    discount_amount    decimal(10, 2) default 0.00              null comment '实际折扣金额（使用时计算并存储）',
    create_time        timestamp      default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        timestamp      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_coupon_code
        unique (coupon_code),
    constraint fk_user_coupon_template
        foreign key (coupon_template_id) references coupon_template (id)
)
    comment '用户优惠券表' charset = utf8mb4;

create index idx_account
    on user_coupon (account);

create index idx_account_status
    on user_coupon (account, status);

create index idx_expire_time
    on user_coupon (expire_time);

create index idx_status
    on user_coupon (status);

create index idx_template_id
    on user_coupon (coupon_template_id);

create definer = root@`%` view order_status_summary as
select `bookstore`.`bookorder`.`orderStatus`                                                              AS `orderStatus`,
       count(0)                                                                                           AS `total_count`,
       sum((case
                when (cast(`bookstore`.`bookorder`.`orderTime` as date) = curdate()) then 1
                else 0 end))                                                                              AS `today_count`,
       sum((case
                when (cast(`bookstore`.`bookorder`.`orderTime` as date) >= (curdate() - interval 7 day)) then 1
                else 0 end))                                                                              AS `week_count`,
       avg((case
                when (`bookstore`.`bookorder`.`orderStatus` = '宸插畬鎴') then timestampdiff(HOUR,
                                                                                             `bookstore`.`bookorder`.`orderTime`,
                                                                                             `bookstore`.`bookorder`.`confirmTime`)
                else NULL end))                                                                           AS `avg_completion_hours`
from `bookstore`.`bookorder`
where (`bookstore`.`bookorder`.`beUserDelete` = 0)
group by `bookstore`.`bookorder`.`orderStatus`;

-- comment on column order_status_summary.orderStatus not supported: 订单状态

create definer = root@`%` view user_available_coupons as
select `uc`.`id`                                                                            AS `user_coupon_id`,
       `uc`.`account`                                                                       AS `account`,
       `uc`.`coupon_code`                                                                   AS `coupon_code`,
       `ct`.`name`                                                                          AS `coupon_name`,
       `ct`.`type`                                                                          AS `type`,
       `ct`.`discount_value`                                                                AS `discount_value`,
       `ct`.`min_order_amount`                                                              AS `min_order_amount`,
       `ct`.`max_discount_amount`                                                           AS `max_discount_amount`,
       `uc`.`receive_time`                                                                  AS `receive_time`,
       `uc`.`expire_time`                                                                   AS `expire_time`,
       (case
            when (`ct`.`type` = 1) then concat('满', `ct`.`min_order_amount`, '减', `ct`.`discount_value`)
            when (`ct`.`type` = 2) then concat((`ct`.`discount_value` / 10), '折优惠') end) AS `discount_desc`
from (`bookstore`.`user_coupon` `uc` join `bookstore`.`coupon_template` `ct`
      on ((`uc`.`coupon_template_id` = `ct`.`id`)))
where ((`uc`.`status` = 1) and (`uc`.`expire_time` > now()) and (`ct`.`status` = 1));

-- comment on column user_available_coupons.user_coupon_id not supported: 用户优惠券ID

-- comment on column user_available_coupons.account not supported: 用户账号

-- comment on column user_available_coupons.coupon_code not supported: 优惠券码（自动生成）

-- comment on column user_available_coupons.coupon_name not supported: 优惠券名称

-- comment on column user_available_coupons.type not supported: 优惠券类型：1-满减券，2-折扣券

-- comment on column user_available_coupons.discount_value not supported: 折扣值（满减券为减免金额，折扣券为折扣百分比，如85表示8.5折）

-- comment on column user_available_coupons.min_order_amount not supported: 最低消费金额

-- comment on column user_available_coupons.max_discount_amount not supported: 最大折扣金额（仅折扣券使用）

-- comment on column user_available_coupons.receive_time not supported: 领取时间

-- comment on column user_available_coupons.expire_time not supported: 过期时间

