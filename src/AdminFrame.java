import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class AdminFrame extends JFrame {
    private Connection conn;

    public AdminFrame(Connection conn) {
        this.conn = conn;
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JButton initDbButton = new JButton("Initialize Database");
        initDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeDatabase();
            }
        });
        panel.add(initDbButton);

        JButton viewTablesButton = new JButton("View All Tables");
        viewTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllTables();
            }
        });
        panel.add(viewTablesButton);

        JButton insertButton = new JButton("Insert Data");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        panel.add(insertButton);

        JButton deleteOrUpdateButton = new JButton("Delete/Update Data");
        deleteOrUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrUpdateData();
            }
        });
        panel.add(deleteOrUpdateButton);

        add(panel);
    }

    private void initializeDatabase() {
        String[] dropForeignKeyConstraints = {
            "ALTER TABLE `티켓` DROP FOREIGN KEY `fk_티켓_예매1`;",
            "ALTER TABLE `티켓` DROP FOREIGN KEY `fk_티켓_상영일정1`;",
            "ALTER TABLE `티켓` DROP FOREIGN KEY `fk_티켓_상영관1`;",
            "ALTER TABLE `티켓` DROP FOREIGN KEY `fk_티켓_좌석1`;",
            "ALTER TABLE `예매` DROP FOREIGN KEY `fk_예매_회원1`;",
            "ALTER TABLE `상영일정` DROP FOREIGN KEY `fk_상영일정_상영관`;",
            "ALTER TABLE `상영일정` DROP FOREIGN KEY `fk_상영일정_영화1`;",
            "ALTER TABLE `좌석` DROP FOREIGN KEY `fk_좌석_상영관1`;"
        };

        String[] dropTables = {
            "DROP TABLE IF EXISTS `티켓`;",
            "DROP TABLE IF EXISTS `예매`;",
            "DROP TABLE IF EXISTS `좌석`;",
            "DROP TABLE IF EXISTS `상영일정`;",
            "DROP TABLE IF EXISTS `회원`;",
            "DROP TABLE IF EXISTS `상영관`;",
            "DROP TABLE IF EXISTS `영화`;"
        };

        String[] createTables = {
            // 영화 테이블
            "CREATE TABLE IF NOT EXISTS `영화` (" +
                    "`영화번호` INT NOT NULL AUTO_INCREMENT," +
                    "`영화명` VARCHAR(255) NOT NULL," +
                    "`상영시간` INT NOT NULL," +
                    "`상영등급` VARCHAR(50) NOT NULL," +
                    "`감독명` VARCHAR(100) NOT NULL," +
                    "`배우명` TEXT NOT NULL," +
                    "`장르` VARCHAR(100) NOT NULL," +
                    "`영화소개` TEXT NOT NULL," +
                    "`개봉일자` DATE NULL," +
                    "`평점` FLOAT NULL," +
                    "`썸네일경로` VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (`영화번호`)) ENGINE = InnoDB;",
            // 상영관 테이블
            "CREATE TABLE IF NOT EXISTS `상영관` (" +
                    "`상영관번호` INT NOT NULL AUTO_INCREMENT," +
                    "`가로좌석수` INT NOT NULL," +
                    "`세로좌석수` INT NOT NULL," +
                    "`상영관사용여부` TINYINT NOT NULL," +
                    "PRIMARY KEY (`상영관번호`)) ENGINE = InnoDB;",
            // 상영일정 테이블
            "CREATE TABLE IF NOT EXISTS `상영일정` (" +
                    "`상영일정번호` INT NOT NULL AUTO_INCREMENT," +
                    "`상영시작일` DATE NOT NULL," +
                    "`상영요일` VARCHAR(10) NOT NULL," +
                    "`상영회차` INT NOT NULL," +
                    "`상영시작시간` TIME NOT NULL," +
                    "`상영관번호` INT NOT NULL," +
                    "`영화번호` INT NOT NULL," +
                    "PRIMARY KEY (`상영일정번호`)," +
                    "INDEX `fk_상영일정_상영관_idx` (`상영관번호` ASC) VISIBLE," +
                    "INDEX `fk_상영일정_영화1_idx` (`영화번호` ASC) VISIBLE," +
                    "CONSTRAINT `fk_상영일정_상영관` " +
                    "FOREIGN KEY (`상영관번호`) " +
                    "REFERENCES `상영관` (`상영관번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION," +
                    "CONSTRAINT `fk_상영일정_영화1` " +
                    "FOREIGN KEY (`영화번호`) " +
                    "REFERENCES `영화` (`영화번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION) ENGINE = InnoDB;",
            // 좌석 테이블
            "CREATE TABLE IF NOT EXISTS `좌석` (" +
                    "`좌석번호` INT NOT NULL AUTO_INCREMENT," +
                    "`좌석사용여부` TINYINT NOT NULL," +
                    "`상영관번호` INT NOT NULL," +
                    "PRIMARY KEY (`좌석번호`)," +
                    "INDEX `fk_좌석_상영관1_idx` (`상영관번호` ASC) VISIBLE," +
                    "CONSTRAINT `fk_좌석_상영관1` " +
                    "FOREIGN KEY (`상영관번호`) " +
                    "REFERENCES `상영관` (`상영관번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION) ENGINE = InnoDB;",
            // 회원 테이블
            "CREATE TABLE IF NOT EXISTS `회원` (" +
                    "`회원번호` INT NOT NULL AUTO_INCREMENT," +
                    "`회원아이디` VARCHAR(50) NOT NULL," +
                    "`고객명` VARCHAR(100) NOT NULL," +
                    "`휴대폰번호` VARCHAR(20) NOT NULL," +
                    "`전자메일주소` VARCHAR(255) NULL," +
                    "PRIMARY KEY (`회원번호`)) ENGINE = InnoDB;",
            // 예매 테이블
            "CREATE TABLE IF NOT EXISTS `예매` (" +
                    "`예매번호` INT NOT NULL AUTO_INCREMENT," +
                    "`결제방법` VARCHAR(50) NOT NULL," +
                    "`결제상태` VARCHAR(20) NOT NULL," +
                    "`결제금액` DECIMAL(10,2) NOT NULL," +
                    "`결제일자` DATE NOT NULL," +
                    "`회원번호` INT NOT NULL," +
                    "PRIMARY KEY (`예매번호`)," +
                    "INDEX `fk_예매_회원1_idx` (`회원번호` ASC) VISIBLE," +
                    "CONSTRAINT `fk_예매_회원1` " +
                    "FOREIGN KEY (`회원번호`) " +
                    "REFERENCES `회원` (`회원번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION) ENGINE = InnoDB;",
            // 티켓 테이블
            "CREATE TABLE IF NOT EXISTS `티켓` (" +
                    "`티켓번호` INT NOT NULL AUTO_INCREMENT," +
                    "`발권여부` TINYINT NOT NULL," +
                    "`표준가격` DECIMAL(10,2) NOT NULL," +
                    "`판매가격` DECIMAL(10,2) NOT NULL," +
                    "`예매번호` INT NOT NULL," +
                    "`상영일정번호` INT NOT NULL," +
                    "`상영관번호` INT NOT NULL," +
                    "`좌석번호` INT NOT NULL," +
                    "PRIMARY KEY (`티켓번호`)," +
                    "INDEX `fk_티켓_예매1_idx` (`예매번호` ASC) VISIBLE," +
                    "INDEX `fk_티켓_상영일정1_idx` (`상영일정번호` ASC) VISIBLE," +
                    "INDEX `fk_티켓_상영관1_idx` (`상영관번호` ASC) VISIBLE," +
                    "INDEX `fk_티켓_좌석1_idx` (`좌석번호` ASC) VISIBLE," +
                    "CONSTRAINT `fk_티켓_예매1` " +
                    "FOREIGN KEY (`예매번호`) " +
                    "REFERENCES `예매` (`예매번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION," +
                    "CONSTRAINT `fk_티켓_상영일정1` " +
                    "FOREIGN KEY (`상영일정번호`) " +
                    "REFERENCES `상영일정` (`상영일정번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION," +
                    "CONSTRAINT `fk_티켓_상영관1` " +
                    "FOREIGN KEY (`상영관번호`) " +
                    "REFERENCES `상영관` (`상영관번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION," +
                    "CONSTRAINT `fk_티켓_좌석1` " +
                    "FOREIGN KEY (`좌석번호`) " +
                    "REFERENCES `좌석` (`좌석번호`) " +
                    "ON DELETE NO ACTION " +
                    "ON UPDATE NO ACTION) ENGINE = InnoDB;"
        };


        String[] insertSampleData = {
                // 영화 테이블 샘플 데이터
                "INSERT INTO `영화` (`영화명`, `상영시간`, `상영등급`, `감독명`, `배우명`, `장르`, `영화소개`, `개봉일자`, `평점`, `썸네일경로`) VALUES " +
                        "('다크나이트', 120, 'PG', 'Director 1', 'Actor 1', 'Genre 1', 'Description 1', '2023-01-01', 4.5, 'https://cdn.topstarnews.net/news/photo/202311/15417917_1200496_4021.jpg')," +
                        "('범죄도시 4', 130, 'PG-13', 'Director 2', 'Actor 2', 'Genre 2', 'Description 2', '2023-02-01', 4.0, 'https://upload.wikimedia.org/wikipedia/ko/6/6a/%EB%B2%94%EC%A3%84%EB%8F%84%EC%8B%9C4_%EB%A9%94%EC%9D%B8_%ED%8F%AC%EC%8A%A4%ED%84%B0.jpg')," +
                        "('어벤져스: 엔드게임', 110, 'R', 'Director 3', 'Actor 3', 'Genre 3', 'Description 3', '2023-03-01', 3.5, 'https://file.mk.co.kr/meet/neds/2019/04/image_readtop_2019_245869_15557132063718502.jpg')," +
                        "('서울의 봄', 100, 'G', 'Director 4', 'Actor 4', 'Genre 4', 'Description 4', '2023-04-01', 4.2, 'https://upload.wikimedia.org/wikipedia/ko/7/76/%EC%84%9C%EC%9A%B8%EC%9D%98_%EB%B4%84_%ED%8F%AC%EC%8A%A4%ED%84%B0.jpg')," +
                        "('파묘', 140, 'PG', 'Director 5', 'Actor 5', 'Genre 5', 'Description 5', '2023-05-01', 4.1, 'https://thumbnews.nateimg.co.kr/view610///news.nateimg.co.kr/orgImg/jn/2024/02/08/9fe9b57b5780da.jpg')," +
                        "('노량: 죽음의 바다', 90, 'PG-13', 'Director 6', 'Actor 6', 'Genre 6', 'Description 6', '2023-06-01', 3.8, 'https://t1.daumcdn.net/news/202312/18/xportsnews/20231218091311485rvjz.jpg')," +
                        "('탑건: 매버릭', 95, 'R', 'Director 7', 'Actor 7', 'Genre 7', 'Description 7', '2023-07-01', 4.7, 'https://upload.wikimedia.org/wikipedia/ko/a/a4/%ED%83%91%EA%B1%B4_%EB%A7%A4%EB%B2%84%EB%A6%AD_%ED%8F%AC%EC%8A%A4%ED%84%B0.jpg')," +
                        "('닥터 스트레인지: 대혼돈의 멀티버스', 105, 'G', 'Director 8', 'Actor 8', 'Genre 8', 'Description 8', '2023-08-01', 4.6, 'https://dimg.donga.com/wps/SPORTS/IMAGE/2022/04/08/112760890.1.jpg')," +
                        "('스파이더맨: 노 웨이 홈', 115, 'PG', 'Director 9', 'Actor 9', 'Genre 9', 'Description 9', '2023-09-01', 4.3, 'https://blog.kakaocdn.net/dn/bcC628/btrniUKNn8f/YMwr1o2Fmu6ulLJoipQgS1/img.jpg')," +
                        "('설계자', 125, 'PG-13', 'Director 10', 'Actor 10', 'Genre 10', 'Description 10', '2023-10-01', 4.4, 'https://i.namu.news/20240508si/a20911f301e82a117615e868eed9a9ce67eb80f380c76e72b12207e67616eb30.jpg')," +
                        "('원더랜드', 135, 'R', 'Director 11', 'Actor 11', 'Genre 11', 'Description 11', '2023-11-01', 3.9, 'https://cdn.topstarnews.net/news/photo/202405/15489550_1305344_1244.jpg')," +
                        "('극한직업', 145, 'G', 'Director 12', 'Actor 12', 'Genre 12', 'Description 12', '2023-12-01', 4.0, 'https://img.extmovie.com/files/attach/images/148/945/184/043/bc63ca961fe3b6610e63882acf9f1a0c.jpg');",

                // 상영관 테이블 샘플 데이터
                "INSERT INTO `상영관` (`가로좌석수`, `세로좌석수`, `상영관사용여부`) VALUES " +
                		"(3, 3, 1)," +
                		"(4, 4, 1)," +
                		"(5, 5, 1)," +
                		"(6, 6, 1)," +
                		"(2, 2, 1)," +
                		"(3, 3, 1)," +
                		"(4, 4, 1)," +
                		"(5, 5, 1)," +
                		"(6, 6, 1)," +
                		"(4, 4, 1)," +
                		"(5, 5, 1)," +
                		"(10, 10, 1);",

                // 상영일정 테이블 샘플 데이터
                "INSERT INTO `상영일정` (`상영시작일`, `상영요일`, `상영회차`, `상영시작시간`, `상영관번호`, `영화번호`) VALUES " +
                        "('2023-01-01', 'Monday', 1, '09:00:00', 1, 1)," +
                        "('2023-01-01', 'Monday', 2, '13:00:00', 2, 1)," +
                        "('2023-01-01', 'Monday', 3, '17:00:00', 3, 1)," +

                        "('2023-01-02', 'Tuesday', 1, '10:00:00', 1, 2)," +
                        "('2023-01-02', 'Tuesday', 2, '14:00:00', 2, 2)," +
                        "('2023-01-02', 'Tuesday', 3, '18:00:00', 3, 2)," +

                        "('2023-01-03', 'Wednesday', 1, '11:00:00', 1, 3)," +
                        "('2023-01-03', 'Wednesday', 2, '15:00:00', 2, 3)," +
                        "('2023-01-03', 'Wednesday', 3, '19:00:00', 3, 3)," +

                        "('2023-01-04', 'Thursday', 1, '12:00:00', 1, 4)," +
                        "('2023-01-04', 'Thursday', 2, '16:00:00', 2, 4)," +
                        "('2023-01-04', 'Thursday', 3, '20:00:00', 3, 4)," +

                        "('2023-01-05', 'Friday', 1, '13:00:00', 1, 5)," +
                        "('2023-01-05', 'Friday', 2, '17:00:00', 2, 5)," +
                        "('2023-01-05', 'Friday', 3, '21:00:00', 3, 5)," +

                        "('2023-01-06', 'Saturday', 1, '09:30:00', 4, 6)," +
                        "('2023-01-06', 'Saturday', 2, '13:30:00', 5, 6)," +
                        "('2023-01-06', 'Saturday', 3, '17:30:00', 6, 6)," +

                        "('2023-01-07', 'Sunday', 1, '10:30:00', 4, 7)," +
                        "('2023-01-07', 'Sunday', 2, '14:30:00', 5, 7)," +
                        "('2023-01-07', 'Sunday', 3, '18:30:00', 6, 7)," +

                        "('2023-01-08', 'Monday', 1, '11:30:00', 7, 8)," +
                        "('2023-01-08', 'Monday', 2, '15:30:00', 8, 8)," +
                        "('2023-01-08', 'Monday', 3, '19:30:00', 9, 8)," +

                        "('2023-01-09', 'Tuesday', 1, '12:30:00', 7, 9)," +
                        "('2023-01-09', 'Tuesday', 2, '16:30:00', 8, 9)," +
                        "('2023-01-09', 'Tuesday', 3, '20:30:00', 9, 9)," +

                        "('2023-01-10', 'Wednesday', 1, '13:30:00', 10, 10)," +
                        "('2023-01-10', 'Wednesday', 2, '17:30:00', 11, 10)," +
                        "('2023-01-10', 'Wednesday', 3, '21:30:00', 12, 10)," +

                        "('2023-01-11', 'Thursday', 1, '14:30:00', 10, 11)," +
                        "('2023-01-11', 'Thursday', 2, '18:30:00', 11, 11)," +
                        "('2023-01-11', 'Thursday', 3, '22:30:00', 12, 11)," +

                        "('2023-01-12', 'Friday', 1, '15:30:00', 1, 12)," +
                        "('2023-01-12', 'Friday', 2, '19:30:00', 2, 12)," +
                        "('2023-01-12', 'Friday', 3, '23:30:00', 3, 12);",

                // 좌석 테이블 샘플 데이터
                "INSERT INTO `좌석` (`좌석사용여부`, `상영관번호`) VALUES " +
                		"(1, 1)," + "(1, 1)," + "(1, 1)," + "(1, 1)," + "(1, 1)," +
                		"(1, 1)," + "(1, 1)," + "(1, 1)," + "(1, 1)," + 
								                        
						"(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," +
						"(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," +
						"(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," + "(1, 2)," +
						"(1, 2)," + 
								                        
						"(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," +
						"(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," +
						"(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," +
						"(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," +
						"(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," + "(1, 3)," +
								                        
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + 
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," + "(1, 4)," +
						"(1, 4)," +
								                        
						"(1, 5)," + "(1, 5)," + "(1, 5)," + "(1, 5)," +
								                        
						"(1, 6)," + "(1, 6)," + "(1, 6)," + "(1, 6)," + "(1, 6)," +
						"(1, 6)," + "(1, 6)," + "(1, 6)," + "(1, 6)," +
								                        
						"(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," +
						"(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," +
						"(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," + "(1, 7)," +
						"(1, 7)," +
                                                        
						"(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," +
						"(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," +
						"(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," +
						"(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," +
						"(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," + "(1, 8)," +
                                                        
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + 
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," + "(1, 9)," +
						"(1, 9)," +
								                        
						"(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," +
						"(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," + 	
						"(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," + "(1, 10)," +
						"(1, 10)," + 
                                                        
						"(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," +
						"(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," +
						"(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," +
						"(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," +
						"(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," + "(1, 11)," +
								                        
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," +   
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," +   
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," +  
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + 
						"(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12)," + "(1, 12);",

                // 회원 테이블 샘플 데이터
                "INSERT INTO `회원` (`회원아이디`, `고객명`, `휴대폰번호`, `전자메일주소`) VALUES " +
                        "('user1', 'User One', '010-1111-1111', 'user1@example.com')," +
                        "('user2', 'User Two', '010-2222-2222', 'user2@example.com')," +
                        "('user3', 'User Three', '010-3333-3333', 'user3@example.com')," +
                        "('user4', 'User Four', '010-4444-4444', 'user4@example.com')," +
                        "('user5', 'User Five', '010-5555-5555', 'user5@example.com')," +
                        "('user6', 'User Six', '010-6666-6666', 'user6@example.com')," +
                        "('user7', 'User Seven', '010-7777-7777', 'user7@example.com')," +
                        "('user8', 'User Eight', '010-8888-8888', 'user8@example.com')," +
                        "('user9', 'User Nine', '010-9999-9999', 'user9@example.com')," +
                        "('user10', 'User Ten', '010-1010-1010', 'user10@example.com')," +
                        "('user11', 'User Eleven', '010-1111-1112', 'user11@example.com')," +
                        "('user12', 'User Twelve', '010-1212-1212', 'user12@example.com');",

                // 예매 테이블 샘플 데이터
                "INSERT INTO `예매` (`결제방법`, `결제상태`, `결제금액`, `결제일자`, `회원번호`) VALUES " +
                        "('Credit Card', 'Completed', 10000.00, '2023-01-01', 1)," +
                        "('Credit Card', 'Completed', 20000.00, '2023-01-02', 2)," +
                        "('Credit Card', 'Completed', 30000.00, '2023-01-03', 3)," +
                        "('Credit Card', 'Completed', 40000.00, '2023-01-04', 4)," +
                        "('Credit Card', 'Completed', 50000.00, '2023-01-05', 5)," +
                        "('Credit Card', 'Completed', 60000.00, '2023-01-06', 6)," +
                        "('Credit Card', 'Completed', 70000.00, '2023-01-07', 7)," +
                        "('Credit Card', 'Completed', 80000.00, '2023-01-08', 8)," +
                        "('Credit Card', 'Completed', 90000.00, '2023-01-09', 9)," +
                        "('Credit Card', 'Completed', 100000.00, '2023-01-10', 10)," +
                        "('Credit Card', 'Completed', 110000.00, '2023-01-11', 11)," +
                        "('Credit Card', 'Completed', 120000.00, '2023-01-12', 12);",

                // 티켓 테이블 샘플 데이터
                "INSERT INTO `티켓` (`발권여부`, `표준가격`, `판매가격`, `예매번호`, `상영일정번호`, `상영관번호`, `좌석번호`) VALUES " +
                        "(1, 12000.00, 12000.00, 1, 1, 1, 3)," +
                        "(1, 12000.00, 12000.00, 2, 5, 2, 16)," +
                        "(1, 12000.00, 12000.00, 3, 7, 1, 4)," +
                        "(1, 12000.00, 12000.00, 4, 10, 1, 5)," +
                        "(1, 12000.00, 12000.00, 5, 14, 2, 15)," +
                        "(1, 12000.00, 12000.00, 6, 16, 4, 71)," +
                        "(1, 12000.00, 12000.00, 7, 20, 5, 88)," +
                        "(1, 12000.00, 12000.00, 8, 23, 8, 128)," +
                        "(1, 12000.00, 12000.00, 9, 26, 8, 129)," +
                        "(1, 12000.00, 12000.00, 10, 29, 11, 200)," +
                        "(1, 12000.00, 12000.00, 11, 33, 12, 288)," +
                        "(1, 12000.00, 12000.00, 12, 35, 2, 19);"
            };
        

        try (Statement stmt = conn.createStatement()) {
            for (String query : dropForeignKeyConstraints) {
                stmt.executeUpdate(query);
            }
            for (String query : dropTables) {
                stmt.executeUpdate(query);
            }
            for (String query : createTables) {
                stmt.executeUpdate(query);
            }
            for (String sql : insertSampleData) {
                stmt.executeUpdate(sql);
            }
            JOptionPane.showMessageDialog(this, "Database initialized successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database initialization failed");
        }
    }


    private void viewAllTables() {
        String[] tableNames = {"영화", "상영관", "상영일정", "좌석", "회원", "예매", "티켓"};
        StringBuilder result = new StringBuilder();

        try (Statement stmt = conn.createStatement()) {
            for (String tableName : tableNames) {
                result.append("Table: ").append(tableName).append("\n");
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
                    var metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(metaData.getColumnName(i)).append(": ").append(rs.getString(i)).append(" ");
                        }
                        result.append("\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to retrieve data from table: " + tableName);
                }
                result.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create statement or connect to the database.");
        }

        JTextArea textArea = new JTextArea(result.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JFrame frame = new JFrame("All Tables");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(scrollPane);
        frame.setVisible(true);
    }


    public void insertData() {
        JFrame frame = new JFrame("Insert Data");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String[] tables = {"영화", "상영관", "상영일정", "좌석", "회원", "예매", "티켓"};
        for (String table : tables) {
            JButton button = new JButton("Insert " + table);
            button.addActionListener(e -> openInsertFrame(table));
            panel.add(button);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void openInsertFrame(String tableName) {
        JFrame insertFrame = new JFrame("Insert Data into " + tableName);
        insertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertFrame.setSize(500, 600);
        insertFrame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        switch (tableName) {
            case "영화":
                setup영화Form(insertFrame, gbc);
                break;
            case "상영관":
                setup상영관Form(insertFrame, gbc);
                break;
            case "상영일정":
                setup상영일정Form(insertFrame, gbc);
                break;
            case "좌석":
                setup좌석Form(insertFrame, gbc);
                break;
            case "회원":
                setup회원Form(insertFrame, gbc);
                break;
            case "예매":
                setup예매Form(insertFrame, gbc);
                break;
            case "티켓":
                setup티켓Form(insertFrame, gbc);
                break;
        }

        insertFrame.setVisible(true);
    }

    private void setup영화Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 영화번호Field = new JTextField(20);
        JTextField 영화명Field = new JTextField(20);
        JTextField 상영시간Field = new JTextField(20);
        JTextField 상영등급Field = new JTextField(20);
        JTextField 감독명Field = new JTextField(20);
        JTextField 배우명Field = new JTextField(20);
        JTextField 장르Field = new JTextField(20);
        JTextField 영화소개Field = new JTextField(20);
        JTextField 개봉일자Field = new JTextField(20);
        JTextField 평점Field = new JTextField(20);
        JTextField 썸네일경로Field = new JTextField(20);

        addField(frame, gbc, "영화번호:", 영화번호Field);
        addField(frame, gbc, "영화명:", 영화명Field);
        addField(frame, gbc, "상영시간:", 상영시간Field);
        addField(frame, gbc, "상영등급:", 상영등급Field);
        addField(frame, gbc, "감독명:", 감독명Field);
        addField(frame, gbc, "배우명:", 배우명Field);
        addField(frame, gbc, "장르:", 장르Field);
        addField(frame, gbc, "영화소개:", 영화소개Field);
        addField(frame, gbc, "개봉일자:", 개봉일자Field);
        addField(frame, gbc, "평점:", 평점Field);
        addField(frame, gbc, "썸네일경로:", 썸네일경로Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`영화` (영화번호, 영화명, 상영시간, 상영등급, 감독명, 배우명, 장르, 영화소개, 개봉일자, 평점, 썸네일경로) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(영화번호Field.getText()));
                pstmt.setString(2, 영화명Field.getText());
                pstmt.setInt(3, Integer.parseInt(상영시간Field.getText()));
                pstmt.setString(4, 상영등급Field.getText());
                pstmt.setString(5, 감독명Field.getText());
                pstmt.setString(6, 배우명Field.getText());
                pstmt.setString(7, 장르Field.getText());
                pstmt.setString(8, 영화소개Field.getText());
                pstmt.setString(9, 개봉일자Field.getText());
                pstmt.setFloat(10, Float.parseFloat(평점Field.getText()));
                pstmt.setString(11, 썸네일경로Field.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup상영관Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 상영관번호Field = new JTextField(20);
        JTextField 좌석수Field = new JTextField(20);
        JTextField 상영관사용여부Field = new JTextField(20);

        addField(frame, gbc, "상영관번호:", 상영관번호Field);
        addField(frame, gbc, "좌석수:", 좌석수Field);
        addField(frame, gbc, "상영관사용여부:", 상영관사용여부Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`상영관` (상영관번호, 좌석수, 상영관사용여부) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(상영관번호Field.getText()));
                pstmt.setInt(2, Integer.parseInt(좌석수Field.getText()));
                pstmt.setInt(3, Integer.parseInt(상영관사용여부Field.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup상영일정Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 상영일정번호Field = new JTextField(20);
        JTextField 상영시작일Field = new JTextField(20);
        JTextField 상영요일Field = new JTextField(20);
        JTextField 상영회차Field = new JTextField(20);
        JTextField 상영시작시간Field = new JTextField(20);
        JTextField 상영관번호Field = new JTextField(20);
        JTextField 영화번호Field = new JTextField(20);

        addField(frame, gbc, "상영일정번호:", 상영일정번호Field);
        addField(frame, gbc, "상영시작일:", 상영시작일Field);
        addField(frame, gbc, "상영요일:", 상영요일Field);
        addField(frame, gbc, "상영회차:", 상영회차Field);
        addField(frame, gbc, "상영시작시간:", 상영시작시간Field);
        addField(frame, gbc, "상영관번호:", 상영관번호Field);
        addField(frame, gbc, "영화번호:", 영화번호Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`상영일정` (상영일정번호, 상영시작일, 상영요일, 상영회차, 상영시작시간, 상영관번호, 영화번호) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(상영일정번호Field.getText()));
                pstmt.setString(2, 상영시작일Field.getText());
                pstmt.setString(3, 상영요일Field.getText());
                pstmt.setInt(4, Integer.parseInt(상영회차Field.getText()));
                pstmt.setString(5, 상영시작시간Field.getText());
                pstmt.setInt(6, Integer.parseInt(상영관번호Field.getText()));
                pstmt.setInt(7, Integer.parseInt(영화번호Field.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup좌석Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 좌석번호Field = new JTextField(20);
        JTextField 좌석사용여부Field = new JTextField(20);
        JTextField 상영관번호Field = new JTextField(20);

        addField(frame, gbc, "좌석번호:", 좌석번호Field);
        addField(frame, gbc, "좌석사용여부:", 좌석사용여부Field);
        addField(frame, gbc, "상영관번호:", 상영관번호Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`좌석` (좌석번호, 좌석사용여부, 상영관번호) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(좌석번호Field.getText()));
                pstmt.setInt(2, Integer.parseInt(좌석사용여부Field.getText()));
                pstmt.setInt(3, Integer.parseInt(상영관번호Field.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup회원Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 회원번호Field = new JTextField(20);
        JTextField 회원아이디Field = new JTextField(20);
        JTextField 고객명Field = new JTextField(20);
        JTextField 휴대폰번호Field = new JTextField(20);
        JTextField 전자메일주소Field = new JTextField(20);

        addField(frame, gbc, "회원번호:", 회원번호Field);
        addField(frame, gbc, "회원아이디:", 회원아이디Field);
        addField(frame, gbc, "고객명:", 고객명Field);
        addField(frame, gbc, "휴대폰번호:", 휴대폰번호Field);
        addField(frame, gbc, "전자메일주소:", 전자메일주소Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`회원` (회원번호, 회원아이디, 고객명, 휴대폰번호, 전자메일주소) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(회원번호Field.getText()));
                pstmt.setString(2, 회원아이디Field.getText());
                pstmt.setString(3, 고객명Field.getText());
                pstmt.setString(4, 휴대폰번호Field.getText());
                pstmt.setString(5, 전자메일주소Field.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup예매Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 예매번호Field = new JTextField(20);
        JTextField 결제방법Field = new JTextField(20);
        JTextField 결제상태Field = new JTextField(20);
        JTextField 결제금액Field = new JTextField(20);
        JTextField 결제일자Field = new JTextField(20);
        JTextField 회원번호Field = new JTextField(20);

        addField(frame, gbc, "예매번호:", 예매번호Field);
        addField(frame, gbc, "결제방법:", 결제방법Field);
        addField(frame, gbc, "결제상태:", 결제상태Field);
        addField(frame, gbc, "결제금액:", 결제금액Field);
        addField(frame, gbc, "결제일자:", 결제일자Field);
        addField(frame, gbc, "회원번호:", 회원번호Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`예매` (예매번호, 결제방법, 결제상태, 결제금액, 결제일자, 회원번호) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(예매번호Field.getText()));
                pstmt.setString(2, 결제방법Field.getText());
                pstmt.setString(3, 결제상태Field.getText());
                pstmt.setDouble(4, Double.parseDouble(결제금액Field.getText()));
                pstmt.setString(5, 결제일자Field.getText());
                pstmt.setInt(6, Integer.parseInt(회원번호Field.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void setup티켓Form(JFrame frame, GridBagConstraints gbc) {
        JTextField 티켓번호Field = new JTextField(20);
        JTextField 발권여부Field = new JTextField(20);
        JTextField 표준가격Field = new JTextField(20);
        JTextField 판매가격Field = new JTextField(20);
        JTextField 예매번호Field = new JTextField(20);
        JTextField 상영일정번호Field = new JTextField(20);
        JTextField 상영관번호Field = new JTextField(20);
        JTextField 좌석번호Field = new JTextField(20);

        addField(frame, gbc, "티켓번호:", 티켓번호Field);
        addField(frame, gbc, "발권여부:", 발권여부Field);
        addField(frame, gbc, "표준가격:", 표준가격Field);
        addField(frame, gbc, "판매가격:", 판매가격Field);
        addField(frame, gbc, "예매번호:", 예매번호Field);
        addField(frame, gbc, "상영일정번호:", 상영일정번호Field);
        addField(frame, gbc, "상영관번호:", 상영관번호Field);
        addField(frame, gbc, "좌석번호:", 좌석번호Field);

        addSaveCancelButton(frame, gbc, e -> {
            String query = "INSERT INTO `db1`.`티켓` (티켓번호, 발권여부, 표준가격, 판매가격, 예매번호, 상영일정번호, 상영관번호, 좌석번호) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(티켓번호Field.getText()));
                pstmt.setInt(2, Integer.parseInt(발권여부Field.getText()));
                pstmt.setDouble(3, Double.parseDouble(표준가격Field.getText()));
                pstmt.setDouble(4, Double.parseDouble(판매가격Field.getText()));
                pstmt.setInt(5, Integer.parseInt(예매번호Field.getText()));
                pstmt.setInt(6, Integer.parseInt(상영일정번호Field.getText()));
                pstmt.setInt(7, Integer.parseInt(상영관번호Field.getText()));
                pstmt.setInt(8, Integer.parseInt(좌석번호Field.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Data inserted successfully");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to insert data: " + ex.getMessage());
            }
        });
    }

    private void addField(JFrame frame, GridBagConstraints gbc, String label, JTextField textField) {
        gbc.gridwidth = 1;
        frame.add(new JLabel(label), gbc);
        gbc.gridx++;
        frame.add(textField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addSaveCancelButton(JFrame frame, GridBagConstraints gbc, ActionListener saveAction) {
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(saveAction);

        cancelButton.addActionListener(e -> frame.dispose());

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        frame.add(saveButton, gbc);

        gbc.gridx = 1;
        frame.add(cancelButton, gbc);
    }

    public void deleteOrUpdateData() {
        JFrame modifyFrame = new JFrame("Delete or Update Data");
        modifyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modifyFrame.setSize(500, 400);
        modifyFrame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField tableNameField = new JTextField(20);
        JTextField conditionField = new JTextField(20);
        JTextField updateField = new JTextField(20);

        addField(modifyFrame, gbc, "Table Name:", tableNameField);
        addField(modifyFrame, gbc, "Condition:", conditionField);
        addField(modifyFrame, gbc, "Update:", updateField);

        JButton deleteButton = new JButton("Delete");
        JButton updateButton = new JButton("Update");

        gbc.gridx = 0;
        gbc.gridy++;
        modifyFrame.add(deleteButton, gbc);

        gbc.gridx = 1;
        modifyFrame.add(updateButton, gbc);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String condition = conditionField.getText();

                String query = "DELETE FROM " + tableName + " WHERE " + condition;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(modifyFrame, "Data deleted successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(modifyFrame, "Failed to delete data: " + ex.getMessage());
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String condition = conditionField.getText();
                String update = updateField.getText();

                String query = "UPDATE " + tableName + " SET " + update + " WHERE " + condition;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(modifyFrame, "Data updated successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(modifyFrame, "Failed to update data: " + ex.getMessage());
                }
            }
        });

        modifyFrame.setVisible(true);
    }

}
