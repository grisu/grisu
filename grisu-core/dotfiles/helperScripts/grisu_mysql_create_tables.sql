    create table `grisu`.`file_user`(
        `user_id` bigint not null auto_increment,
       `dn` varchar(255) default '' not null unique,
        primary key (`user_id`)
    );

    create table `grisu`.`file_roots`(
        `user_id` bigint default '0' not null,
       `mount_name` varchar(255) default '' not null,
       `file_root` varchar(255) default '' not null
    );
    
    create table `grisu`.`mountpoints`(
    	`mountPointId` bigint not null auto_increment,
    	`user_id` bigint not null,
    	`dn` varchar(255) default '' not null,
    	`fqan` varchar(255) default '',
    	`rootUrl` varchar(255) default '' not null,
    	`mountpoint` varchar(255) default '' not null,
    	`automaticallyMounted` bool  not null,
    	`disabled` bool not null,
    	primary key (`mountPointId`)
    );
    
    create table `grisu`.`fqans` (
    	`user_id` bigint not null,
    	`fqan` varchar(255),
    	`vo` varchar(255)
    );
    
    create table `grisu`.`jobproperties` (
    	`job_id` bigint not null,
    	`prop_key` varchar(255),
    	`prop_value` varchar(255)
    );
    
    create table `grisu`.`file_transfers`(
        `transfer_id` bigint not null auto_increment,
       `user_id` bigint not null,
	   `user_index` int not null,
       `source_url` varchar(255) default '' not null,
       `target_url` varchar(255) default '' not null,
       `status` int default '-1' not null,
        primary key (`transfer_id`)
    );
	
    create table `grisu`.`file_reservations`(
        `reservation_id` bigint not null auto_increment,
        `user_id` bigint not null,
        `user_index` int not null,
       `file_url` varchar(255) default '' not null,
       `file_size` int default '0' not null,
       `status` int default '0',
       `valid_until` date,
        primary key (`reservation_id`)
    );
    
    create table `grisu`.`credential`(
        `credential_id` bigint not null auto_increment,
       `dn` varchar(255) default '' not null,
       `fqan` varchar(255),
       `expiryDate` datetime not null,
       `isRenewable` BIT default '' not null,
       `credentialData` varbinary(5000) not null,
        primary key (`credential_id`)
    );

    create table `grisu`.`jobs`(
        `id` bigint not null auto_increment,
       `jobname` varchar(255) default '' not null,
       `dn` varchar(255) default '' not null,
       `fqan` varchar(255),
       `jsdl` text,
       `submittedJobDescription` text,
       `jobhandle` varchar(255),
       `submissionHost` varchar(255),
       `submissionType` varchar(255),
       `status` int default '-1' not null,
       `credential_id` bigint,
       `application` varchar(255),
       `stdout` varchar(255),
       `stderr` varchar(255),
       `job_directory` varchar(255),
        primary key (`id`)
    );
    
