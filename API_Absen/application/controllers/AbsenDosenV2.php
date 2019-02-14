<?php
/**
 * Created by PhpStorm.
 * User: mr-lvs
 * Date: 13/02/19
 * Time: 10:05
 */

class AbsenDosenV2 extends CI_Controller
{

	//0012096409
	public function __construct()
	{
		parent::__construct();
		$this->load->helper('url');
		$this->load->library('session');
		$this->load->model('Query');
		date_default_timezone_set('Asia/Jakarta');
		header('Access-Control-Allow-Origin: *');
		header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE");
		header('Content-Type: application/json');
	}

	public function index(){
		$data = "Api is working";
		json_encode($data);
	}

	public function getPilihJadwal(){
		//thn semester
		//$thnsem = $this -> Query -> orderByLimit('thnakademik','thnsem','DESC', '1') -> row();

		/*
		 *
		 * SELECT
  `jadwaldossplit20182`.`Id`,
  `jadwaldossplit20182`.`ThnSmester`,
  `jadwaldossplit20182`.`Kd_hari`,
  `jadwaldossplit20182`.`JamAwal`,
  `jadwaldossplit20182`.`JamAkhir`,
  `jadwaldossplit20182`.`Ruang`,
  `jadwaldossplit20182`.`NoMk`,
  `matakuliah`.`Nama_MK`,
  `matakuliah`.`Semester`,
  `matakuliah`.`SKS`,
  `jadwaldossplit20182`.`Kelas`,
  `jadwaldossplit20182`.`Penggal`,
  `jadwaldossplit20182`.`NIDN`,
  `jadwaldossplit20182`.`Kd_Program`,
  `jadwaldossplit20182`.`Kd_Jur`,
  `jurusan`.`nama_jurusan`,
  `jurusan`.`singkatanjur` as 'singkatanjur',
  `jadwaldossplit20182`.`JenjangPddk`,
  `jenjang`.`Nama`,
  `jadwaldossplit20182`.`Fak`,
  `fakultas`.`nama_fakultas`,
  `fakultas`.`singkatan` as 'singfak',
  `jadwaldossplit20182`.`Kampus`,
  `jadwaldossplit20182`.`KdJnsSem`,
  if(`jadwaldossplit20182`.`Kd_Program` = 'R','Reguler','EDP') as JNSKELAS,
  if(`jadwaldossplit20182`.`Kd_Program` = 'R','-1','0') as hitung
FROM
  `jadwaldossplit20182`
  INNER JOIN `matakuliah` ON `jadwaldossplit20182`.`NoMk` =
    `matakuliah`.`Kode_MK` AND `jadwaldossplit20182`.`Kd_Jur` =
    `matakuliah`.`JurusanKd`
  INNER JOIN `jenjang` ON `jadwaldossplit20182`.`JenjangPddk` =
    `jenjang`.`Jenjang_ID`
  INNER JOIN `jurusan` ON `jadwaldossplit20182`.`Kd_Jur` =
    `jurusan`.`kode_jurusan`
  INNER JOIN `fakultas` ON `jadwaldossplit20182`.`Fak` =
    `fakultas`.`kode_fakultas` where jadwaldossplit20182.NIDN= '0213108501'
		 */


	}
}
