<?php
/**
 * Created by PhpStorm.
 * User: mr-lvs
 * Date: 01/02/19
 * Time: 16:47
 */

class Absendosen extends CI_Controller
{

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
		$nidn      	= $this->input->post('nidn');
		$nomk      	= $this->input->post('nomk');
		$jamawal	= $this->input->post('jamawal');
		$jamakhir	= $this->input->post('jamakhir');
		$kelas		= $this->input->post('kelas');

		$query  = $this -> Query -> getData(array('NIDN'=>$nidn,
			'NoMk'=>$nomk,
			'Kelas'=>$kelas,
			'JamAwal <='=>$jamawal,
			'JamAkhir >='=>$jamakhir),
			'vjadwaldossplit20181')-> result();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['result'] = $query;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Gagal';
		}
		echo json_encode($data);
	}

	public function getKodeKelas(){
		$nidn      	= $this->input->post('nidn');
		$kdHari     = $this->input->post('kodehari');
		$jamawal	= $this->input->post('jamawal');
		$jamakhir	= $this->input->post('jamakhir');

		$query  = $this -> Query -> getData(array('NIDN'=>$nidn,
			'Kd_hari'=>$kdHari,
			'JamAwal <='=>$jamawal,
			'JamAkhir >='=>$jamakhir),
			'jadwaldossplit20181')-> result();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'berhasil';
			$data['data'] 	= $query;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Anda tidak memiliki jam mengajar atau waktu absen sudah habis, silihkan hubungi admin MIS jika ada masalah. Terimakasih...';
		}
		echo json_encode($data);
	}


	public function parsingDataRuang(){

		$nidn      	= $this->input->post('nidn');
		$kdHari     = $this->input->post('kodehari');
		$jamawal	= $this->input->post('jamawal');
		$jamakhir	= $this->input->post('jamakhir');
		$ruang		= $this->input->post('ruang');

		$query  = $this -> Query -> selectRight(array('NIDN'=>$nidn,
			'Ruang LIKE'=>'%'.$ruang.'%',
			'JamAwal <='=>$jamawal,
			'JamAkhir >='=>$jamakhir,
			'Kd_hari'=>$kdHari),
			'jadwaldossplit20181')-> row();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['result'] = $query->ruangs;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Maaf sepertinya kelas anda salah';
		}
		echo json_encode($data);
	}

	public function getBlnThnabsen(){
		$tglAwal      	= $this->input->post('tglAwal');
		$tglAkhir      	= $this->input->post('tglAkhir');

		$query  = $this -> Query -> getData(array('tglawal <='=>$tglAwal,
			'tglakhir >='=>$tglAkhir),
			'setabsenngajar')-> row();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['blnthn'] 	= $query->blnthn;
			$data['mingguke'] 	= $query->mingguke;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Gagal';
		}
		echo json_encode($data);
	}



	public function inputAbsenDosen(){

		$kdhari         =  $this -> input -> post('kodehari');
		$jamAwal        =  $this -> input -> post('jamawal');
		$jamakhir       =  $this -> input -> post('jamakhir');
		$ruang          =  $this -> input -> post('ruang');
		$kelas          =  $this -> input -> post('kelas');
		$nidn           =  $this -> input -> post('nidn');
		$kdmk           =  $this -> input -> post('kodematakuliah');
		$sks            =  $this -> input -> post('sks');
		$jmlhhadir      =  $this -> input -> post('jmlhadir');
		$blnthnabsen    =  $this -> input -> post('blntahunabsen');
		$kdProdi        =  $this -> input -> post('kodeprodi');
		$mingguke       =  $this -> input -> post('minggike');
		$program  		=  $this -> input -> post('program');
		$operator       =  $this -> input -> post('operator');
		$thnSem         =  $this -> input -> post('thnsemester');
		$idjadwal       =  $this -> input -> post('idjadwal');

		$cekabsen = $this -> Query -> getData(array('nidn'=>$nidn,
													'kdmk'=>$kdmk,
													'kdhari'=>$kdhari,
													'tglabsen'=>date('Y-m-d')),
									'absenngajar20181')-> row();


		if($cekabsen):
			$data['status'] = false;
			$data['msg']	= 'Anda sudah absen';
		else:
			$data['status'] = true;
			$insert = $this -> Query -> inputData(array(
				'kdhari' 	 		=> $kdhari,
				'tglabsen' 	  	 	=> date('Y-m-d'),
				'jamawal' 	 		=> $jamAwal,
				'jamakhir' 			=> $jamakhir,
				'ruang' 	  	 	=> $ruang,
				'kelas' 	 		=> $kelas,
				'nidn' 	  	 		=> $nidn,
				'kdmk' 	 			=> $kdmk,
				'sks' 				=> $sks,
				'jmlhadir' 	  	 	=> $jmlhhadir,
				'blnthnabsen' 	 	=> $blnthnabsen,
				'kdprodi' 			=> $kdProdi,
				'mingguke' 	  	 	=> $mingguke,
				'program' 	 		=> $program,
				'operator' 	  	 	=> $operator,
				'hitung' 	 		=> '-1',
				'thnsem' 			=> $thnSem,
				'idjadwal' 	  	 	=> $idjadwal,
				'tglinput'			=> date('Y-m-d H:i:s'),
				'programby'			=> 'qrcode'
			),'absenngajar20181');

			if ($insert):
				$data['status'] = true;
				$data['msg']    = "Berhasil Absen";
			else:
				$data['status'] = false;
				$data['msg']	= 'Gagal Absen';
			endif;
		endif;
		echo json_encode($data);
	}


	public function inputBAD(){
		$beritaAcara	=  $this -> input -> post('beritaacara');
		$kdmk       	=  $this -> input -> post('kdmk');
		$mingguKe       =  $this -> input -> post('mingguke');
		$blnThnabsn     =  $this -> input -> post('blnthn');

		$update = $this -> Query -> updateData(array('kdmk'=>$kdmk, 'mingguke'=>$mingguKe,'blnthnabsen'=>$blnThnabsn),
											   array('beritaacara'=>$beritaAcara),
											   'absenngajar20181');
		if ($update):
			$data['status'] = true;
			$data['msg']    = "Berhasil input BAD";
		else:
			$data['status'] = false;
			$data['msg']	= 'Gagal Input BAD';
		endif;
		echo json_encode($data);
	}


}
