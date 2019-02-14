<?php
/**
 * Created by PhpStorm.
 * User: mr-lvs
 * Date: 13/02/19
 * Time: 22:16
 */

class Bad extends CI_Controller
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
		$thnsem = $this -> Query -> orderByLimit('thnakademik','thnsem','DESC', '1') -> row();
		$nidn      	= $this->input->post('nidn');


		$query  = $this -> Query -> orderByWhere(array('NIDN'=>$nidn),
			'vjadwaldossplit20181','Kd_hari', 'ASC')-> result();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['result'] = $query;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Anda tidak memiliki jadwal mengajar disemester ini';
		}
		echo json_encode($data);
	}

	public function listAbsen(){
		$thnsem = $this -> Query -> orderByLimit('thnakademik','thnsem','DESC', '1') -> row();
		$nidn      	= $this->input->post('nidn');
		$nomk      	= $this->input->post('nomk');
		$kodehari	= $this->input->post('kodehari');
		$kelas		= $this->input->post('kelas');


		$query  = $this -> Query -> orderByWhere(array('nidn'=>$nidn,
			'kdmk'=>$nomk,
			'kelas'=>$kelas,
			'kdhari'=>$kodehari),
			'absenngajar20181', 'mingguke', 'ASC')-> result();

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

	public function selectBad(){
		$thnsem = $this -> Query -> orderByLimit('thnakademik','thnsem','DESC', '1') -> row();
		$nidn      	= $this->input->post('nidn');
		$nomk      	= $this->input->post('nomk');
		$kodehari	= $this->input->post('kodehari');
		$kelas		= $this->input->post('kelas');
		$minggu		= $this->input->post('mingguke');


		$query  = $this -> Query -> getData(array('nidn'=>$nidn,
			'kdmk'=>$nomk,
			'kelas'=>$kelas,
			'kdhari'=>$kodehari,
			'mingguke'=>$minggu,),
			'absenngajar20181')-> row();

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


	public function editBAD(){
		$thnsem = $this -> Query -> orderByLimit('thnakademik','thnsem','DESC', '1') -> row();
		$beritaAcara	=  $this -> input -> post('beritaacara');
		$kdmk       	=  $this -> input -> post('kdmk');
		$mingguKe       =  $this -> input -> post('mingguke');
		$blnThnabsn     =  $this -> input -> post('blnthn');
		$nidn           =  $this -> input -> post('nidn');
		$kdhari         =  $this -> input -> post('kdhari');

		$update = $this -> Query -> updateData(array('nidn'=>$nidn,
												'kdmk'=>$kdmk,
												'mingguke'=>$mingguKe,
												'blnthnabsen'=>$blnThnabsn,
												'kdhari'=>$kdhari),
			array('beritaacara'=>$beritaAcara),
			'absenngajar20181');
		if ($update):
			$data['status'] = true;
			$data['msg']    = "Berhasil edit BAD";
		else:
			$data['status'] = false;
			$data['msg']	= 'Gagal Input BAD';
		endif;
		echo json_encode($data);
	}
}
