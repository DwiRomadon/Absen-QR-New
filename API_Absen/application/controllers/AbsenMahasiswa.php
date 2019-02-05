<?php
/**
 * Created by PhpStorm.
 * User: mr-lvs
 * Date: 01/02/19
 * Time: 14:32
 */

class AbsenMahasiswa extends CI_Controller
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

		$kdmk      		= $this->input->post('kdmk');

		$query  = $this -> Query -> getData(array('kdmk'=>$kdmk,
												  'tglabsen'=>date('Y-m-d')),
			 									  'absenngajar20181')-> row();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['idabsenngajar'] 	= $query->Id;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Gagal';
		}
		echo json_encode($data);
	}


	public function getDataKrs(){

		$npm      			= $this->input->post('npm');
		$kodemk      		= $this->input->post('kodemk');
		$jamAwal      		= $this->input->post('jamAwal');
		$jamakhir      		= $this->input->post('jamAkhir');
		$kls      		    = $this->input->post('kls');

		$query = $this -> Query -> getData(array(	'nimhstrnlm'=>$npm,
			'Kode'=>$kodemk,
			'JamAwal <='=>$jamAwal,
			'JamAkhir >='=>$jamakhir,
			'Kls'=>$kls)
			,'vkrsdetail20181')->result();


		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['data'] 	= $query;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Anda tidak memiliki jam kuliah';
		}
		echo json_encode($data);

	}

	public function getDataKrsKelas(){

		$npm      			= $this->input->post('npm');
		$jamAwal      		= $this->input->post('jamawal');
		$jamakhir      		= $this->input->post('jamakhir');
		$kdHari      		= $this->input->post('kodehari');

		$query = $this -> Query -> getData(array(	'nimhstrnlm'=>$npm,
				'JamAwal <='=>$jamAwal,
				'JamAkhir >='=>$jamakhir,
				'Kd_hari'=>$kdHari)
			,'vkrsdetail20181')->result();


		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['data'] 	= $query;
		}else{
			$data['status'] = false;
			$data['msg']	= 'anda Tidak memiliki jam kuliah';
		}
		echo json_encode($data);

	}


	public function parsingDataRuangMhs(){

		$nidn      	= $this->input->post('npm');
		$kdHari     = $this->input->post('kodehari');
		$jamawal	= $this->input->post('jamawal');
		$jamakhir	= $this->input->post('jamakhir');
		$ruang		= $this->input->post('ruang');

		$query  = $this -> Query -> selectRight(array('nimhstrnlm'=>$nidn,
			'Ruang LIKE'=>'%'.$ruang.'%',
			'JamAwal <='=>$jamawal,
			'JamAkhir >='=>$jamakhir,
			'Kd_hari'=>$kdHari),
			'vkrsdetail20181')-> row();

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


	public function inputAbsenMhs(){
		$pertemuangKe     	= $this->input->post('pertemuangKe');
		$npm				= $this->input->post('npm');
		$kodeMk				= $this->input->post('kodeMk');
		$Kelas				= $this->input->post('Kelas');

		$cekabsen = $this -> Query -> getData(array('npm'=>$npm,
			'kdmk'=>$kodeMk,
			'pertemuanke'=>$pertemuangKe,
			'tglabsen'=>date('Y-m-d')),
			'absenmhs20181')-> row();

		if($cekabsen):
			$data['status'] = false;
			$data['msg']	= 'Anda sudah absen dipertemuan ini';
		else:
			$data['status'] = true;
			$query  = $this -> Query -> getData(array('kdmk'=>$kodeMk,
				'tglabsen'=>date('Y-m-d')),
				'absenngajar20181')-> row();

			if ($query):
				$data['status'] = true;
				$insert = $this -> Query -> inputData(array(
								'idabsenngajar' 	=> $query->Id,
								'tglabsen' 	  	 	=> date('Y-m-d'),
								'pertemuanke' 	 	=> $pertemuangKe,
								'npm' 				=> $npm,
								'kdmk' 	  	 		=> $kodeMk,
								'kelas' 	 		=> $Kelas,
								'jmlhadir' 	  	 	=> '1',
								'tglinput' 	 		=> date('Y-m-d H:i:s'),
				),'absenmhs20181');
				if ($insert):
					$data['status'] = true;
					$data['msg']    = "Berhasil Absen";
				else:
					$data['status'] = false;
					$data['msg']	= 'Gagal Absen';
				endif;
			else:
				$data['status'] = false;
				$data['msg']	= 'Dosen belum memulai kelas atau dosen blm melakukan absen';
			endif;
		endif;
		echo json_encode($data);
	}
}
