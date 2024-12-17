--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3
-- Dumped by pg_dump version 16.3

-- Started on 2024-12-17 09:34:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 216 (class 1259 OID 16661)
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    category_id integer NOT NULL,
    user_id integer NOT NULL,
    category_name character varying NOT NULL
);


ALTER TABLE public.category OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16666)
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.category_category_id_seq OWNER TO postgres;

--
-- TOC entry 4894 (class 0 OID 0)
-- Dependencies: 217
-- Name: category_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_category_id_seq OWNED BY public.category.category_id;


--
-- TOC entry 218 (class 1259 OID 16667)
-- Name: mini_test; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mini_test (
    test_id integer NOT NULL,
    user_id integer NOT NULL,
    test_name character varying(50)
);


ALTER TABLE public.mini_test OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16670)
-- Name: mini_test_test_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mini_test_test_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.mini_test_test_id_seq OWNER TO postgres;

--
-- TOC entry 4895 (class 0 OID 0)
-- Dependencies: 219
-- Name: mini_test_test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mini_test_test_id_seq OWNED BY public.mini_test.test_id;


--
-- TOC entry 215 (class 1259 OID 16658)
-- Name: more_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.more_category (
    category_id integer NOT NULL,
    question_id integer NOT NULL
);


ALTER TABLE public.more_category OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16671)
-- Name: more_category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.more_category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.more_category_category_id_seq OWNER TO postgres;

--
-- TOC entry 4896 (class 0 OID 0)
-- Dependencies: 220
-- Name: more_category_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.more_category_category_id_seq OWNED BY public.more_category.category_id;


--
-- TOC entry 221 (class 1259 OID 16672)
-- Name: more_mini_test; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.more_mini_test (
    test_id integer NOT NULL,
    question_id integer NOT NULL
);


ALTER TABLE public.more_mini_test OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16675)
-- Name: more_mini_test_test_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.more_mini_test_test_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.more_mini_test_test_id_seq OWNER TO postgres;

--
-- TOC entry 4897 (class 0 OID 0)
-- Dependencies: 222
-- Name: more_mini_test_test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.more_mini_test_test_id_seq OWNED BY public.more_mini_test.test_id;


--
-- TOC entry 225 (class 1259 OID 16723)
-- Name: question; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.question (
    question_id integer NOT NULL,
    user_id integer NOT NULL,
    question_type character varying(20) NOT NULL,
    question character varying NOT NULL,
    answer character varying NOT NULL,
    choices text[],
    created_at timestamp without time zone NOT NULL
);


ALTER TABLE public.question OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16735)
-- Name: question_question_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.question_question_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.question_question_id_seq OWNER TO postgres;

--
-- TOC entry 4898 (class 0 OID 0)
-- Dependencies: 226
-- Name: question_question_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.question_question_id_seq OWNED BY public.question.question_id;


--
-- TOC entry 223 (class 1259 OID 16682)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    email character varying(225) NOT NULL,
    password character varying(128) NOT NULL,
    created_at timestamp with time zone
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16685)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 4899 (class 0 OID 0)
-- Dependencies: 224
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4714 (class 2604 OID 16686)
-- Name: category category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category ALTER COLUMN category_id SET DEFAULT nextval('public.category_category_id_seq'::regclass);


--
-- TOC entry 4715 (class 2604 OID 16687)
-- Name: mini_test test_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mini_test ALTER COLUMN test_id SET DEFAULT nextval('public.mini_test_test_id_seq'::regclass);


--
-- TOC entry 4713 (class 2604 OID 16688)
-- Name: more_category category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.more_category ALTER COLUMN category_id SET DEFAULT nextval('public.more_category_category_id_seq'::regclass);


--
-- TOC entry 4716 (class 2604 OID 16689)
-- Name: more_mini_test test_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.more_mini_test ALTER COLUMN test_id SET DEFAULT nextval('public.more_mini_test_test_id_seq'::regclass);


--
-- TOC entry 4718 (class 2604 OID 16736)
-- Name: question question_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.question ALTER COLUMN question_id SET DEFAULT nextval('public.question_question_id_seq'::regclass);


--
-- TOC entry 4717 (class 2604 OID 16691)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 4878 (class 0 OID 16661)
-- Dependencies: 216
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.category (category_id, user_id, category_name) FROM stdin;
\.


--
-- TOC entry 4880 (class 0 OID 16667)
-- Dependencies: 218
-- Data for Name: mini_test; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mini_test (test_id, user_id, test_name) FROM stdin;
\.


--
-- TOC entry 4877 (class 0 OID 16658)
-- Dependencies: 215
-- Data for Name: more_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.more_category (category_id, question_id) FROM stdin;
\.


--
-- TOC entry 4883 (class 0 OID 16672)
-- Dependencies: 221
-- Data for Name: more_mini_test; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.more_mini_test (test_id, question_id) FROM stdin;
\.


--
-- TOC entry 4887 (class 0 OID 16723)
-- Dependencies: 225
-- Data for Name: question; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.question (question_id, user_id, question_type, question, answer, choices, created_at) FROM stdin;
7	1	4択問題	どのプラットフォームを用いて、ファイルからテキストを抽出しますか？	グーグルドライブ	{[Dropbox,OneDrive,iCloud,グーグルドライブ]}	2024-12-16 14:12:54.013436
8	1	4択問題	テキスト抽出を行う前に必要な操作は何ですか？	文字を抽出したいPDFまたは画像ファイルをアップロード	{[文字を読みとる,テキストをPDFに変換,画像を編集,文字を抽出したいPDFまたは画像ファイルをアップロード]}	2024-12-16 14:12:54.013436
\.


--
-- TOC entry 4885 (class 0 OID 16682)
-- Dependencies: 223
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, email, password, created_at) FROM stdin;
1	sample@example.com	$2a$10$q3bWUsg2JEFU1Od64dtJsOe5ji30jeypMIbdkh4Wt4IEFp0MVqz2W	2024-12-05 10:16:17.809347+09
8	user_1@example.com	$2a$10$d36Xe95XMEQfMrMUkkOECeXKOTpMoN8LwFY6YuWhxeWQeKs1zbo6S	2024-12-09 13:39:08.242797+09
9	user2@example.com	$2a$10$ihDys4ScE1ld6cPGm/yGlOzE8nsIXNNyIwAOJc.aeoms0YhPEZm4i	2024-12-09 14:06:11.556022+09
10	user_3@example.com	$2a$10$t6Xx6SDBQ6kaYGVaDeSPA.2Na6PkrGnpJ4qvIBDZWtvNvnyLGHrZ6	2024-12-09 14:56:47.302056+09
\.


--
-- TOC entry 4900 (class 0 OID 0)
-- Dependencies: 217
-- Name: category_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.category_category_id_seq', 1, false);


--
-- TOC entry 4901 (class 0 OID 0)
-- Dependencies: 219
-- Name: mini_test_test_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mini_test_test_id_seq', 1, false);


--
-- TOC entry 4902 (class 0 OID 0)
-- Dependencies: 220
-- Name: more_category_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.more_category_category_id_seq', 1, false);


--
-- TOC entry 4903 (class 0 OID 0)
-- Dependencies: 222
-- Name: more_mini_test_test_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.more_mini_test_test_id_seq', 1, false);


--
-- TOC entry 4904 (class 0 OID 0)
-- Dependencies: 226
-- Name: question_question_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.question_question_id_seq', 8, true);


--
-- TOC entry 4905 (class 0 OID 0)
-- Dependencies: 224
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 10, true);


--
-- TOC entry 4722 (class 2606 OID 16693)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4724 (class 2606 OID 16695)
-- Name: mini_test mini_test_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mini_test
    ADD CONSTRAINT mini_test_pkey PRIMARY KEY (test_id);


--
-- TOC entry 4720 (class 2606 OID 16697)
-- Name: more_category more_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.more_category
    ADD CONSTRAINT more_category_pkey PRIMARY KEY (category_id, question_id);


--
-- TOC entry 4726 (class 2606 OID 16699)
-- Name: more_mini_test more_mini_test_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.more_mini_test
    ADD CONSTRAINT more_mini_test_pkey PRIMARY KEY (test_id, question_id);


--
-- TOC entry 4730 (class 2606 OID 16729)
-- Name: question question_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.question
    ADD CONSTRAINT question_pkey PRIMARY KEY (question_id);


--
-- TOC entry 4728 (class 2606 OID 16703)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4731 (class 2606 OID 16704)
-- Name: category category_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4732 (class 2606 OID 16709)
-- Name: mini_test mini_test_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mini_test
    ADD CONSTRAINT mini_test_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4733 (class 2606 OID 16730)
-- Name: question questions_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.question
    ADD CONSTRAINT questions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) NOT VALID;


-- Completed on 2024-12-17 09:34:01

--
-- PostgreSQL database dump complete
--

